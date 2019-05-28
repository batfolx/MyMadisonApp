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