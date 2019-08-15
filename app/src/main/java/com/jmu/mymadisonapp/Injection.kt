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

package com.jmu.mymadisonapp

import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jmu.mymadisonapp.data.*
import com.jmu.mymadisonapp.net.MYMADISON_BASE_URL
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.WebViewCookieJar
import com.jmu.mymadisonapp.room.MyMadisonDatabase
import com.jmu.mymadisonapp.ui.MainViewModel
import com.jmu.mymadisonapp.ui.grades.GradesViewModel
import com.jmu.mymadisonapp.ui.home.HomeViewModel
import com.jmu.mymadisonapp.ui.login.LoginViewModel
import com.jmu.mymadisonapp.ui.slideshow.ClassScheduleViewModel
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

// Provide network resources
val netModule = module {

    // Provide a singleton CookieJar so Cookies can be shared between OkHttp/Retrofit and WebView's.
    single<CookieJar> { WebViewCookieJar() }

    single<Interceptor> {
        object : Interceptor {
            val cookieJar: CookieJar = get()
            override fun intercept(chain: Interceptor.Chain): Response =
                chain.proceed(
                    chain.request().newBuilder().apply {
                        cookieJar.loadForRequest(chain.request().url())
                            .forEach { cookie -> addHeader("Cookie", cookie.toString()) }
                    }.build()
                ).let { response ->
                    cookieJar.saveFromResponse(
                        response.request().url(), Cookie.parseAll(response.request().url(), response.headers())
                        /*.also { log("ResponseCookies", it.joinToString("\nSet-Cookie: ")) }*/
                    )
                    response
                }
        }
    }

    // Provide a singleton OkHttpClient to be shared with Retrofit and custom network requests.
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addNetworkInterceptor(get())
            .addNetworkInterceptor(
                HttpLoggingInterceptor { if (!it.startsWith("Cookie")) logD("OkHttp", it, false) }
                    .setLevel(HttpLoggingInterceptor.Level.BASIC)
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
//            .connectionSpecs(mutableListOf(ConnectionSpec))
//            .connectionPool(ConnectionPool(10, 10, TimeUnit.MINUTES))
            .cookieJar(get())
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    // Provide a singleton Retrofit service with the singleton OkHttpClient and the JspoonConverterFactory for parsing HTML responses
    single<MyMadisonService> {
        Retrofit.Builder()
            .client(get())
            .baseUrl(MYMADISON_BASE_URL)
            .addConverterFactory(JspoonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(MyMadisonService::class.java)
    }

}

val databaseModule = module {

    single { Room.databaseBuilder(get(), MyMadisonDatabase::class.java, "mymadison-db").build() }

    single { get<MyMadisonDatabase>().studentDao() }

    single { get<MyMadisonDatabase>().termDao() }

}

// Provide data sources and repositories for ViewModel's
val appModule = module {
    single { LoginDataSource(get(), get()) }

    single { LoginRepository(get()) }

    single { ClassScheduleRepository(get(), get()) }

    single { TermRepository(get(), get()) }

    single { StudentRepository(get(), get()) }

    viewModel { ClassScheduleViewModel(get(), get()) }

    viewModel { HomeViewModel() }

    viewModel { GradesViewModel(get(), get()) }

    viewModel { MainViewModel(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
}