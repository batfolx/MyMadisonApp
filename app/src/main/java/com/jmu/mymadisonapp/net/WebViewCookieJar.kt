package com.jmu.mymadisonapp.net

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class WebViewCookieJar : CookieJar {
    private val cookieManager: CookieManager = CookieManager.getInstance()

    init {
//        cookieManager.removeAllCookies(null)
//        cookieManager.flush()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies)
            cookieManager.setCookie(
                "${if (cookie.secure()) "https" else "http"}://${if (cookie.hostOnly()) url.uri().host else cookie.domain()}${cookie.path()}",
                cookie.toString()
            )
//        log("SavingResponseCookies", cookies.joinToString(";") { it.toString() })
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> =
        with(url.uri()) {
            //            log("LoadingCookies", "Cookies($this)=${cookieManager.getCookie(toString())}")
            cookieManager.getCookie(toString())?.split(";")
//                ?.also { log("All Cookies", it.joinToString(";")) }
                ?.flatMap { cookie ->
                    cookie.split("=").let { if (it.size % 2 == 1) it.dropLast(1) else it }
                        .chunked(2) { (name, value) ->
                            Cookie.Builder()
                                .apply { if (url.isHttps) secure() }
                                .path(path)
                                .domain(host)
                                .name(name.trim())
                                .value(value.trim())
                                .build()
                        }
                } ?: emptyList()/*?: { log("CookieJar", "Weird Cookie!! $cookie"); null }()*/
        }.filterNotNull().toMutableList()
//            .also { log("LoadingRequestCookies", it.joinToString(";") { it.toString() }) }

}