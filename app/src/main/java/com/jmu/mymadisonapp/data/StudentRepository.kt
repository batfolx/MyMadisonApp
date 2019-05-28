package com.jmu.mymadisonapp.data

import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.checkLoggedIn

class StudentRepository(private val client: MyMadisonService) {

    suspend fun isLoggedIn() = checkLoggedIn()

    suspend fun getUndergraduateDashboard() = client.getUndergraduateDashboard().await()

    suspend fun getCanvasProfileInfo() =
        client.getSAMLResponse().await().body()?.value?.let {
            client.getCanvasProfileInfo(it).await()
        }

    suspend fun getMyGradeTerms() = client.getMyGradeTerms().await()

    suspend fun getMyGradesForTerm(body: Map<String, String>) = client.getMyGradesForTerm(body).await()

}