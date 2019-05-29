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

package com.jmu.mymadisonapp.ui.gallery

import android.app.Application
import androidx.lifecycle.*
import com.jmu.mymadisonapp.data.GradeRepository
import com.jmu.mymadisonapp.net.Loading
import com.jmu.mymadisonapp.net.Success
import com.jmu.mymadisonapp.room.model.Term
import kotlinx.coroutines.launch

class GradesViewModel(application: Application, private val repository: GradeRepository) :
    AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val gradesLiveData: MediatorLiveData<List<Term>> = MediatorLiveData()

    val text: LiveData<String> = _text

    fun getAllGrades() {
        viewModelScope.launch {
            gradesLiveData.addSource(repository.getAllMyGrades()) {
                when (it) {
                    is Success -> {
                        loadingLiveData.postValue(false)
                        gradesLiveData.postValue(it.data)
                    }
                    is Loading -> {
                        loadingLiveData.postValue(true)
                        it.data?.let { terms -> gradesLiveData.postValue(terms) }
                    }
                    is Error -> {
                        loadingLiveData.postValue(false)
                    }
                }
            }
        }
        /*viewModelScope.launch {
            with(getMyGradeTerms() ?: GradeTerms()) {
                log(
                    "AllMyGrades",
                    (0 until this.terms.size).map { this.terms[it] to getMyGradesForTerm(this, it) }.joinToString("\n")
                )
            }
        }*/
    }

    /* private suspend fun getMyGradeTerms() =
         repository.getMyGradeTerms().also {
             log(
                 "MyGradeTerms",
                 "Terms: ${it.body() ?: "Empty body: $it"}"
             )
         }.body()

     private suspend fun getMyGradesForTerm(gradeTerms: GradeTerms, term: Int) =
         repository.getMyGradesForTerm(gradeTerms.termPostData.toMutableMap().also {
             it["ICAction"] = "DERIVED_SSS_SCT_SSR_PB_GO"; it["ICBcDomData"] = "UnknownValue"
         } + ("SSR_DUMMY_RECV1\$sels\$$term\$\$0" to "$term")).also {
             log(
                 "MyGradesForTerm",
                 "Term=${gradeTerms.terms[term]}\n${it.body() ?: "Empty body: $it"}"
             )
         }.body()
 */

}