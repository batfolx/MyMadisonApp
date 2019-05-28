package com.jmu.mymadisonapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jmu.mymadisonapp.data.StudentRepository
import com.jmu.mymadisonapp.data.model.StudentUndergradInfo
import com.jmu.mymadisonapp.log
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val repository: StudentRepository) :
    AndroidViewModel(application) {

    // Observable LiveData for the result of getting the Undergraduate Dashboard information.
    val undergradInfoLiveData: MutableLiveData<StudentUndergradInfo> = MutableLiveData()

    suspend fun isLoggedIn(): Boolean =
        try {
            repository.getUndergraduateDashboard(); true
        } catch (e: IllegalArgumentException) {
            false
        }

    /**
     * Requests Undergraduate Dashboard data from the repository and posts it to any observers.
     */
    fun getUndergraduateDashboard() {
        viewModelScope.launch {
            undergradInfoLiveData.postValue(repository.getUndergraduateDashboard().also {
                log(
                    "UndergraduateDashboard",
                    "Content: ${
                    try {
                        it.body() ?: "Empty body: $it"
                    } catch (e: java.lang.IllegalArgumentException) {
                        e.printStackTrace()
                    }
                    }}"
                )
            }.body())

        }
    }

}