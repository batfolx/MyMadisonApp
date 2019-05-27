package com.jmu.mymadisonapp

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jmu.mymadisonapp.data.MYMADISON_BASE_URL
import com.jmu.mymadisonapp.data.MyMadisonService
import com.jmu.mymadisonapp.net.WebViewCookieJar
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val netModule = module {

    single<CookieJar> { WebViewCookieJar }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor(
                    HttpLoggingInterceptor.Logger { message ->
                        Log.d("OkHttp", message)
                    })
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addInterceptor {
                it.proceed(with(it.request().newBuilder()) {
                    get<CookieJar>().loadForRequest(it.request().url())
                        .forEach {
                            addHeader("Cookie", it.toString())
                        }
                    build()
                })
            }
            .addInterceptor {
                with(it.proceed(it.request())) {
                    get<CookieJar>().saveFromResponse(
                        request().url(), Cookie.parseAll(request().url(), headers()))
                    this
                }
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .cache(null)
            .cookieJar(get())
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    single<MyMadisonService> {
        Retrofit.Builder()
            .client(get())
            .baseUrl(MYMADISON_BASE_URL)
            .addConverterFactory(JspoonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(MyMadisonService::class.java)
    }

}

