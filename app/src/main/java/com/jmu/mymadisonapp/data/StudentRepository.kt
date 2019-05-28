package com.jmu.mymadisonapp.data

class StudentRepository(private val client: MyMadisonService) {


    suspend fun getUndergraduateDashboard() =
        client.getUndergraduateDashboard().await()
//        client.getStudentInfo(mapOf("cmd" to "getCachedPglt", "pageletname" to "JMU_UG_DB", "tab" to "STUDENTADMINISTRATION", "PORTALPARAM_COMPWIDTH" to "Narrow", "bNoGlobal" to "Y", "ptlayout" to "N")).await()

}