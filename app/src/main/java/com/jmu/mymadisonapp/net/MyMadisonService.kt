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
import com.jmu.mymadisonapp.studentcenter.*
import com.jmu.mymadisonapp.studentcenter.search.ListOfSearchResults
import com.jmu.mymadisonapp.ui.jaccardservices.CardServices
import com.jmu.mymadisonapp.ui.myaccounts.ListOfMealPlans
import com.jmu.mymadisonapp.ui.myaccounts.MealPlans
import com.jmu.mymadisonapp.ui.myaccounts.MyAccountAccounts
import com.jmu.mymadisonapp.ui.myaccounts.MyAccountsFragment
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
const val LOGOUT_COMMAND_URL = "https://mymadison.ps.jmu.edu/psp/pprd/JMU/CUST/?cmd=logout"

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

    @GET(LOGOUT_COMMAND_URL)
    fun logout(): Deferred<Response<ResponseBody>>

    //@FieldMap refers to the Form Data of the request
    @FormUrlEncoded
    @Headers("Sec-Fetch-Mode: cors")
    @POST("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL")
    fun getMyGradesForTerm(@FieldMap termIndex: Map<String, String>): Deferred<Response<ClassGrades>>

    @FormUrlEncoded
    @Headers("Sec-Fetch-Mode: cors")
    @POST("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_LIST.GBL")
    fun getMyClassScheduleForTerm(@FieldMap termIndex: Map<String, String>): Deferred<Response<ClassSchedule>>

    @GET("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SAA_SS_DPR_ADB.GBL")
    fun getAcademicRequirements(): Deferred<Response<ListsOfGradRequirements>>

    @GET("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
    fun getEnrolledClasses(): Deferred<Response<ListOfEnrolledClasses>>

    @FormUrlEncoded
    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/PRJCS_MENU.PRJCS_SCHD_STRT.GBL")
    fun getSchedulePlanner()

    @GET("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
    fun getShoppingCartClasses(): Deferred<Response<ListOfAddEnrollShoppingCart>>

    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
    fun deleteSelectedClass(@Body body: FormBody): Deferred<Response<ResponseBody>> //this is for deleting a specific item in your shopping cart


    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.CLASS_SEARCH.GBL")
    fun getSearchedClasses(@Body body: FormBody): Deferred<Response<ListOfSearchResults>>

    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
    fun addClass(@Body body: FormBody): Deferred<Response<ResponseBody>> //method used to add a class from the list of search results


    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
    fun enrollInAllClasses(@Body body: FormBody): Deferred<Response<ResponseBody>> //method used to add a class from the list of search results

    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_ADD.GBL")
    fun confirmEnrollInAllClasses(@Body body: FormBody): Deferred<Response<ResponseBody>> //for confirming class addition to SHOPPING CART

    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES_2.SSR_SSENRL_CART.GBL")
    fun enrollInClass(@Body body: FormBody): Deferred<Response<ResponseBody>> //this is for enrolling only the classes you chose from the shopping cart

    @Headers("Sec-Fetch-Mode: cors")
    @POST("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_ADD.GBL")
    fun addClassesToSchedule(@Body body: FormBody): Deferred<Response<ListOfSearchResults>>

    /**
     * Function with a @GET annotation that connects the request and returned a Response object with the list
     * of academic course catalogs
     */
    @GET("/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES_2.SSS_BROWSE_CATLG_P.GBL")
    fun getCourseCatalogs(): Deferred<Response<ListOfAcademicCourseCatalogs>>

    /**
     * Get the MyAccount information (eID password expiry and time change)
     */
    @GET("https://mymadison.ps.jmu.edu/psp/pprd/JMU/CUST/h/?cmd=getCachedPglt&pageletname=JMU_ID_CMP&tab=JMU_MYACCOUNT&PORTALPARAM_COMPWIDTH=Wide&bNoGlobal=Y&ptlayout=N")
    fun getMyAccountInformation(): Deferred<Response<MyAccountAccounts>>

    /**
     * Gets information about card services (amount of money in flex, dining, etc
     */
    @GET("https://mymadison.ps.jmu.edu/psp/pprd/JMU/CUST/h/?cmd=getCachedPglt&pageletname=JMU_CARDSVC_SSO&tab=JMU_MYSERVICES&PORTALPARAM_COMPWIDTH=Narrow&bNoGlobal=Y&ptlayout=N")
    fun getCardServicesInformation(): Deferred<Response<CardServices>>

    /**
     * Gets meal plan information (maybe implement later)
     */
    @GET("https://jmu.campusdish.com/MealPlans/Category?cat=All_Products_6598&lid=6598")
    fun getMealPlans(): Deferred<Response<ListOfMealPlans>>



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

