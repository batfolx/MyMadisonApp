package com.jmu.mymadisonapp

import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jmu.mymadisonapp.data.LoginDataSource
import com.jmu.mymadisonapp.data.LoginRepository
import com.jmu.mymadisonapp.data.StudentRepository
import com.jmu.mymadisonapp.net.MYMADISON_BASE_URL
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.WebViewCookieJar
import com.jmu.mymadisonapp.room.MyMadisonDatabase
import com.jmu.mymadisonapp.ui.MainViewModel
import com.jmu.mymadisonapp.ui.login.LoginViewModel
import okhttp3.*
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
                HttpLoggingInterceptor { logD("OkHttp", it, false) }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
//            .connectionSpecs(mutableListOf(ConnectionSpec))
            .connectionPool(ConnectionPool(10, 10, TimeUnit.MINUTES))
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

}

// Provide data sources and repositories for ViewModel's
val appModule = module {

    single { LoginDataSource(get(), get()) }

    single { LoginRepository(get()) }

    single { StudentRepository(get()) }

    viewModel { MainViewModel(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
}