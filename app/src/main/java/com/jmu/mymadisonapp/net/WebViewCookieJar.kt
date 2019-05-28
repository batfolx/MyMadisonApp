package com.jmu.mymadisonapp.net

import android.webkit.CookieManager
import com.jmu.mymadisonapp.log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class WebViewCookieJar : CookieJar {
    private val cookieManager: CookieManager = CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies)
            cookieManager.setCookie(
                (if (cookie.secure()) "https" else "http") + "://" + cookie.domain() + cookie.path(),
                cookie.value()
            )
//        log("SavingResponseCookies", cookies.joinToString(";") { it.toString() })
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> =
        (url.uri().let {
            cookieManager.getCookie(it.toString())?.split(";")
//                ?.also { log("All Cookies", it.joinToString(";")) }
                ?.map { cookie ->
                    cookie.split("=").takeIf { split -> split.size >= 2 }
                        ?.let { (name, value) ->
                            Cookie.Builder()
                                .path(it.path)
                                .domain(it.host)
                                .secure()
                                .name(name.trim())
                                .value(value.trim())
                                .build()
                        } /*?: { log("CookieJar", "Weird Cookie!! $cookie"); null }()*/
                }
        } ?: emptyList()).filterNotNull().toMutableList().also {
            log("LoadingRequestCookies", it.joinToString(";") { it.toString() })
        }

}