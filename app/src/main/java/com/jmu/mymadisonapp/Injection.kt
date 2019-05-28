package com.jmu.mymadisonapp

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jmu.mymadisonapp.data.LoginDataSource
import com.jmu.mymadisonapp.data.LoginRepository
import com.jmu.mymadisonapp.data.StudentRepository
import com.jmu.mymadisonapp.net.MYMADISON_BASE_URL
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.WebViewCookieJar
import com.jmu.mymadisonapp.ui.MainViewModel
import com.jmu.mymadisonapp.ui.login.LoginViewModel
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

// Provide network resources
val netModule = module {

    // Provide a singleton CookieJar so Cookies can be shared between OkHttp/Retrofit and WebView's.
    single<CookieJar> { WebViewCookieJar() }

    // Provide a singleton OkHttpClient to be shared with Retrofit and custom network requests.
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor(
                    HttpLoggingInterceptor.Logger { message ->
                        Log.d("OkHttp", message)
                    })
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            /*.addInterceptor {
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
            }*/
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
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



// Provide data sources and repositories for ViewModel's
val appModule = module {

    single { LoginDataSource(get(), get()) }

    single { LoginRepository(get()) }

    single { StudentRepository(get()) }

    viewModel { MainViewModel(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
}
