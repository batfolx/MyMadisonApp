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
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jmu.mymadisonapp.MyMadisonApp
import com.jmu.mymadisonapp.data.AcademicRequirementsRepository
import com.jmu.mymadisonapp.data.StudentRepository
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.Error
import com.jmu.mymadisonapp.net.Loading
import com.jmu.mymadisonapp.net.Success
import com.jmu.mymadisonapp.room.model.Student
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainViewModel(application: Application, private val repository: StudentRepository) :
    AndroidViewModel(application) {

    // Observable LiveData for the result of getting the Undergraduate Dashboard information.
//    val undergradInfoLiveData: MutableLiveData<StudentUndergradInfo> = MutableLiveData()
//    val userProfileLiveData: MutableLiveData<CanvasProfileInfo> = MutableLiveData()
    val studentInfoLiveData: MediatorLiveData<Student> = MediatorLiveData()
    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    suspend fun isLoggedIn(): Boolean = repository.isLoggedIn()

    fun onLoggedIn() {
        viewModelScope.launch {
            studentInfoLiveData.addSource(repository.getStudentData()) {
                when (it) {
                    is Success -> {
                        loadingLiveData.postValue(false)
                        studentInfoLiveData.postValue(it.data)
                    }
                    is Loading -> {
                        loadingLiveData.postValue(true)
                        it.data?.let { data -> studentInfoLiveData.postValue(data) }
                    }
                    is Error -> loadingLiveData.postValue(false)
                }
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
                "Current User: ${it.body() ?: "Empty body: $it"}"
            )
        }.body()
    } catch (e: IllegalArgumentException) {
        e.printStackTrace(); null
    }

    /**
     * Requests Undergraduate Dashboard data from the repository and posts it to any observers.
     */
//    fun getUndergraduateDashboard() {
//        viewModelScope.launch {
//            undergradInfoLiveData.postValue(getUndergradDash())
//        }
//    }

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


    fun getAcademicRequirements() {
        viewModelScope.launch {
            val arRepository: AcademicRequirementsRepository = getApplication<MyMadisonApp>().get()
            arRepository.getAcademicRequirements()
        }
    }



}