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

package com.jmu.mymadisonapp.net

import com.jmu.mymadisonapp.data.model.*
import com.jmu.mymadisonapp.studentcenter.GraduationRequirements
import com.jmu.mymadisonapp.studentcenter.ListOfEnrolledClasses
import com.jmu.mymadisonapp.studentcenter.ListsOfGradRequirements
import kotlinx.coroutines.Deferred
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.koin.core.context.GlobalContext.get
import retrofit2.Response
import retrofit2.http.*

const val MYMADISON_BASE_URL = "https://mymadison.ps.jmu.edu"
const val MYMADISON_LOGIN_BASE_URL = "https://mymadison.ps.jmu.edu/psp/pprd/?cmd=start"

/**
 * The MyMadison Retrofit service.
 */
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

    @GET("/psp/pprd/JMU/CUST/h/?tab=DEFAULT")
    fun checkLoggedIn(): Deferred<Response<ResponseBody>>

    @GET("/psp/pprd/JMU/CUST/h/")
    fun getStudentInfo(@QueryMap(encoded = false) infoQueries: Map<String, String>, @Query("search") searchQuery: String): Deferred<Response<ResponseBody>>

    /**
     * Get information for the Undergraduate Dashboard.
     */
    @GET("/psp/pprd/JMU/CUST/h/?cmd=getCachedPglt&pageletname=JMU_UG_DB")
    fun getUndergraduateDashboard(): Deferred<Response<StudentUndergradInfo>>

    @GET("/psc/pprd/JMU/CUST/s/WEBLIB_JMU_APPS.JMU_CANVAS.FieldFormula.IScript_Canvas_SSO_Pagelet")
    fun getSAMLResponse(): Deferred<Response<SAMLResponse>>

    @FormUrlEncoded
    @POST("https://canvas.jmu.edu/saml_consume")
    fun getCanvasProfileInfo(@Field("SAMLResponse") samlResponse: String): Deferred<Response<CurrentUser>>


    @GET("/psp/pprd/JMU/SPRD/c/SA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL?pslnkid=JMU_STUDENTCENTER_MAINMENU&FolderPath=PORTAL_ROOT_OBJECT.JMU_STUDENT_MAINMENU.JMU_STUDENTCENTER_MAINMENU&IsFolder=false&IgnoreParamTempl=FolderPath%2cIsFolder")
    fun getStudentCenter(): Deferred<Response<ResponseBody>>

    @GET("/psc/ecampus/JMU/SPRD/c/{terms}")
    fun getTermsList(@Path("terms") terms: String): Deferred<Response<GradeTerms>>

    @FormUrlEncoded
    @Headers("Sec-Fetch-Mode: cors")
    @POST("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL")
    fun getMyGradesForTerm(@FieldMap termIndex: Map<String, String>): Deferred<Response<ClassGrades>>

    @FormUrlEncoded
    @Headers("Sec-Fetch-Mode: cors")
    @POST("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_LIST.GBL")
    fun getMyClassScheduleForTerm(@FieldMap termIndex: Map<String, String>): Deferred<Response<ClassSchedule>>

    @GET("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SAA_SS_DPR_ADB.GBL")
    fun getAcademicRequirements(): Deferred<Response<ListsOfGradRequirements>>

    @GET("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
    fun getEnrolledClasses(): Deferred<Response<ListOfEnrolledClasses>>


//    @GET("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSS_TSRQST_UNOFF.GBL?Page=SSS_TSRQST_UNOFF&Action=A&ExactKeys=Y&TargetFrameName=None")
//    fun getUnofficialTranscript()

}

fun MutableMap<String, String>.updateTermPostBody(termIndex: Int): Map<String, String> {
    this["ICAction"] = "DERIVED_SSS_SCT_SSR_PB_GO"
    this["ICBcDomData"] = "UnknownValue"
    return this + ("SSR_DUMMY_RECV1\$sels\$$termIndex\$\$0" to "$termIndex")
}

fun <T> Response<T>.isValid() = isSuccessful && body() != null

fun checkLoggedIn(): Boolean =
    get().koin.get<OkHttpClient>().newBuilder().followRedirects(false).followSslRedirects(false).build()
        .newCall(
            Request.Builder()
                .url("$MYMADISON_BASE_URL/psp/pprd/JMU/CUST/h/?tab=DEFAULT")
                .get()
                .build()
        )
        .execute().isSuccessful

