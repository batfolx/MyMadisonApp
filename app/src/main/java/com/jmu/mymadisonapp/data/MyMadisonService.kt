package com.jmu.mymadisonapp.data

import com.jmu.mymadisonapp.MainActivity
import com.jmu.mymadisonapp.log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.koin.android.ext.android.inject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.net.URLEncoder

const val MYMADISON_BASE_URL = "https://login.jmu.edu"
const val MYMADISON_LOGIN_BASE_URL = "https://mymadison.ps.jmu.edu/psp/pprd/?cmd=start"

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

}

suspend fun MainActivity.loginUser(eID: String, password: String) =
    withContext(Dispatchers.Default) {
        val mmService: MyMadisonService by inject()
        val responseBody =
            client.newCall(
                Request.Builder()
                    .url(MYMADISON_LOGIN_BASE_URL)
                    .get()
                    .build()
            )
                .execute()
                .body()
                ?.string()
        log("MyMadisonLoginAttempt", "Response: ${responseBody ?: ""}")
        val requestId = Jsoup.parse(responseBody).selectFirst("form#signon > input").`val`()
        log("LoginFormData", "Original = request_id=$requestId&username=$eID&password=$password")
        log("LoginFormData", "URL Encoded = ${URLEncoder.encode("request_id=$requestId&username=$eID&password=$password", "UTF-8")}")

        mmService.loginAsync(FormBody.Builder().add("request_id", requestId).add("username", eID).add("password", password).build())
    }.await()