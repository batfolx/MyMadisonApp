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

import android.icu.text.SimpleDateFormat
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

class ClassScheduleRepository(private val client: MyMadisonService, private val termDao: TermDao) {

    fun getClassSchedules() =
        singleResource<List<Term>> {
            loadFromDb { termDao.getAllTerms() as LiveData<List<Term>?> }
            createCall(::getAllClassSchedules)
            saveResult { termDao.upsertTerms(*it.toTypedArray()); 0 }
        }

    fun getAllClassSchedules() = GlobalScope.async {
        with(getMyClassScheduleTerms().takeIf { it.isValid() }?.body() ?: GradeTerms()) {
            log("InitialScheduleTerms", "Content: $this")
            terms.mapIndexed { index, term ->
                Term(
                    term.term.substringBefore(" "),
                    term.term.substringAfterLast(" ").toInt(),
                    term.career,
                    term.institution,
                    getMyClassScheduleForTerm(termPostData.toMap().toMutableMap().updateTermPostBody(index)).body()?.schedule?.let { classes ->
                        log("ClassSchedule", "Term=($index, $term): $classes")
                        classes.map {
                            Course(
                                it.title.substringBefore(" "),
                                it.title.substringAfter(" ").substringBefore(" "),
                                it.title.substringAfterLast("- "),
                                it.units,
                                it.grading,
                                it.grade,
                                0f,
                                it.status,
                                it.reason,
                                it.classNumber,
                                it.section,
                                it.component,
                                it.datesAndTimes,
                                it.room,
                                it.instructor,
                                it.startEndDate.flatMap { dates ->
                                    dates.split(" - ").chunked(2) { (start, end) ->
                                        with(SimpleDateFormat("MM/dd/yyyy")) {
                                            parse(start) to parse(end)
                                        }
                                    }
                                },
                                it.aidEligible
                            )
                        }
                    } ?: emptyList(),
                    0f, "")
            }.also { log("ClassSchedule", "Content: $it") }
        }.let { Response.success(it) }
    }

    suspend fun getMyClassScheduleTerms() = client.getTermsList("SA_LEARNER_SERVICES.SSR_SSENRL_LIST.GBL").await()

    suspend fun getMyClassScheduleForTerm(body: Map<String, String>) = client.getMyClassScheduleForTerm(body).await()

}