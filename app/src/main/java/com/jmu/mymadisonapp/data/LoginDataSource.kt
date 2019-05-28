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

package com.jmu.mymadisonapp.data

import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MYMADISON_LOGIN_BASE_URL
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.ResponseException
import com.jmu.mymadisonapp.net.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URLEncoder

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val client: OkHttpClient, private val mmService: MyMadisonService) {

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

