package com.jmu.mymadisonapp.data

import com.jmu.mymadisonapp.data.model.StudentUndergradInfo
import kotlinx.coroutines.Deferred
import okhttp3.FormBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

const val MYMADISON_BASE_URL = "https://mymadison.ps.jmu.edu"
const val MYMADISON_LOGIN_BASE_URL = "https://mymadison.ps.jmu.edu/psp/pprd/?cmd=start"
const val MYMADISON_UG_URL =
    "https://mymadison.ps.jmu.edu/psp/pprd/JMU/CUST/h/?cmd=getCachedPglt&pageletname=JMU_UG_DB&tab=STUDENTADMINISTRATION&PORTALPARAM_COMPWIDTH=Narrow&bNoGlobal=Y&ptlayout=N"

interface MyMadisonService {

    @Headers(
        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
        "Accept-Encoding: gzip, deflate, br",
        "Accept-Language: en-US,en;q=0.9",
        "Cache-Control: max-age=0",
        "Connection: keep-alive",
        "Host: login.jmu.edu",
        "Origin: https://login.jmu.edu",
        "Sec-Fetch-Mode: navigate",
        "Sec-Fetch-Site: same-origin",
        "Sec-Fetch-User: ?1",
        "Upgrade-Insecure-Requests: 1",
        "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3800.0 Safari/537.36")
    @POST("/oam/server/auth_cred_submit")
    fun loginAsync(@Body body: FormBody): Deferred<Response<ResponseBody>>

    @GET("/psp/pprd/JMU/CUST/h/")
    fun getStudentInfo(@QueryMap(encoded = false) infoQueries: Map<String, String>): Deferred<Response<ResponseBody>>

    @GET("/psp/pprd/JMU/CUST/h/?cmd=getCachedPglt&pageletname=JMU_UG_DB&tab=STUDENTADMINISTRATION&PORTALPARAM_COMPWIDTH=Narrow&bNoGlobal=Y&ptlayout=N")
    fun getUndergraduateDashboard(): Deferred<Response<StudentUndergradInfo>>

}

