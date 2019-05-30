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

package com.jmu.mymadisonapp.ui.slideshow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jmu.mymadisonapp.data.ClassScheduleRepository
import com.jmu.mymadisonapp.net.Error
import com.jmu.mymadisonapp.net.Loading
import com.jmu.mymadisonapp.net.Success
import com.jmu.mymadisonapp.room.model.Course
import kotlinx.coroutines.launch

class ClassScheduleViewModel(application: Application, val repository: ClassScheduleRepository) :
    AndroidViewModel(application) {


    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val classSchedulesLiveData: MediatorLiveData<List<Course>> = MediatorLiveData()


    fun getAllClassSchedules() {
        viewModelScope.launch {
            classSchedulesLiveData.addSource(repository.getClassSchedules()) {
                when (it) {
                    is Success -> {
                        loadingLiveData.postValue(false)
                        classSchedulesLiveData.postValue(it.data.flatMap { it.courses })
                    }
                    is Loading -> {
                        loadingLiveData.postValue(true)
                        it.data?.let { classSchedulesLiveData.postValue(it.flatMap { it.courses }) }
                    }
                    is Error -> loadingLiveData.postValue(false)
                }
            }
        }
    }
}