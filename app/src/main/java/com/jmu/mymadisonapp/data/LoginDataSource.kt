package com.jmu.mymadisonapp.data

import com.jmu.mymadisonapp.data.model.LoggedInUser
import com.jmu.mymadisonapp.log
import com.timmahh.openph.data.ResponseException
import com.timmahh.openph.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val client: OkHttpClient, private val mmService: MyMadisonService) {

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            Result.success(fakeUser)
        } catch (e: Throwable) {
            Result.error(IOException("Error logging in", e), null)
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    suspend fun loginUser(eID: String, password: String) =
        with(withContext(Dispatchers.Default) {
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
            log(
                "LoginFormData",
                "URL Encoded = ${URLEncoder.encode("request_id=$requestId&username=$eID&password=$password", "UTF-8")}"
            )

            mmService.loginAsync(
                FormBody.Builder().add("request_id", requestId).add("username", eID).add(
                    "password",
                    password
                ).build()
            )
        }.await()) {
            if (isSuccessful) Result.success(this.body())
            else Result.error(ResponseException(this), this.errorBody())
        }
}

