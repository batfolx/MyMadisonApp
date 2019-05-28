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

package com.jmu.mymadisonapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jmu.mymadisonapp.data.StudentRepository
import com.jmu.mymadisonapp.data.model.GradeTerms
import com.jmu.mymadisonapp.data.model.StudentUndergradInfo
import com.jmu.mymadisonapp.log
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val repository: StudentRepository) :
    AndroidViewModel(application) {

    // Observable LiveData for the result of getting the Undergraduate Dashboard information.
    val undergradInfoLiveData: MutableLiveData<StudentUndergradInfo> = MutableLiveData()

    suspend fun isLoggedIn(): Boolean = repository.isLoggedIn()

    fun onLoggedIn() {
        viewModelScope.launch {
            getProfileInfo()
            getUndergradDash()?.let {
                undergradInfoLiveData.postValue(it)
            }
            with(getMyGradeTerms() ?: GradeTerms()) {
                log(
                    "AllMyGrades",
                    (0 until this.terms.size).map { this.terms[it] to getMyGradesForTerm(this, it) }.joinToString("\n")
                )
            }
        }
    }

    fun getCanvasProfileInfo() {
        viewModelScope.launch {
            getProfileInfo()
        }
    }

    private suspend fun getProfileInfo() = try {
        repository.getCanvasProfileInfo().also {
            log(
                "CanvasProfileInfo",
                "Current User: ${it?.body() ?: "Empty body: $it"}"
            )
        }?.body()
    } catch (e: IllegalArgumentException) {
        e.printStackTrace(); null
    }

    /**
     * Requests Undergraduate Dashboard data from the repository and posts it to any observers.
     */
    fun getUndergraduateDashboard() {
        viewModelScope.launch {
            undergradInfoLiveData.postValue(getUndergradDash())
        }
    }

    private suspend fun getUndergradDash() =
        try {
            repository.getUndergraduateDashboard().also {
                log(
                    "UndergraduateDashboard",
                    "Content: ${it.body() ?: "Empty body: $it"}}"
                )
            }.body()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace(); null
        }


    private suspend fun getMyGradeTerms() =
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

}