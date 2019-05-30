/*
 * Copyright 2019 Timothy Logan
 * Copyright 2019 Victor Velea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmu.mymadisonapp.data

import androidx.lifecycle.LiveData
import com.jmu.mymadisonapp.data.model.GradeTerms
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.isValid
import com.jmu.mymadisonapp.net.singleResource
import com.jmu.mymadisonapp.net.updateTermPostBody
import com.jmu.mymadisonapp.room.TermDao
import com.jmu.mymadisonapp.room.model.Course
import com.jmu.mymadisonapp.room.model.Term
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Response

class TermRepository(private val client: MyMadisonService, private val termDao: TermDao) {


    suspend fun getAllMyGrades() =
        singleResource<List<Term>> {
            loadFromDb { termDao.getAllTerms() as LiveData<List<Term>?> }
            createCall(::getAllTerms)
            saveResult { upsertTerms(it); 0 }
            shouldFetch { data: List<Term>? -> data?.isEmpty() ?: true }
        }
//        getResource(
//            loadFromDb = termDao::getAllTerms as () -> LiveData<List<Term>?>,
//            createCall = this::getAllTerms,
//            saveResult = ::upsertTerms,
//            shouldFetch = { data: List<Term>? -> data?.isEmpty() ?: true }
//        )

    private suspend fun upsertTerms(terms: List<Term>) =
        termDao.upsertTerms(*terms.toTypedArray())

    private fun getAllTerms() = GlobalScope.async {
        with(getMyGradeTerms().takeIf { it.isValid() }?.body() ?: GradeTerms()) {
            log("InitialGradeTerms", "Content: $this")
            terms.mapIndexed { index, term ->
                getMyGradesForTerm(termPostData.toMutableMap().updateTermPostBody(index)).body()?.let {
                    Term(
                        term.term.substringBefore(" "),
                        term.term.substringAfterLast(" ").toInt(),
                        term.career,
                        term.institution,
                        it.grades.map { grade ->
                            Course(
                                grade.className.substringBefore(" "),
                                grade.className.substringAfter(" "),
                                grade.description,
                                grade.units,
                                grade.grading,
                                grade.grade,
                                grade.gradePoints
                            )
                        },
                        it.semesterGPA,
                        it.academicStanding
                    )
                } ?: { log("GetGradesForTerm", "Returned null"); null }()
            }.filterNotNull()
                .also { log("AllParsedTerms", "Terms: ${it.joinToString("\n")}") }
        }.let { Response.success(it) }
    }


    suspend fun getMyGradeTerms() = client.getTermsList("SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL").await()

    suspend fun getMyGradesForTerm(body: Map<String, String>) = client.getMyGradesForTerm(body).await()


}