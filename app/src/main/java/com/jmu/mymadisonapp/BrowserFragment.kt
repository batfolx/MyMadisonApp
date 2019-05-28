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


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.jmu.mymadisonapp.net.MYMADISON_LOGIN_BASE_URL
import kotlinx.android.synthetic.main.fragment_browser.*


/**
 * A [Fragment] that displays the MyMadison login page
 */
class BrowserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_browser, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Set the settings for the CookieManager
        with(CookieManager.getInstance()) {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(browser_view, true)
        }

        browser_view.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.onLoggedIn()
            }

            // If isLoggedIn returns true, pop the back stack.
            private fun String.onLoggedIn() = Uri.parse(this).isLoggedIn().ifDo { fragmentManager?.popBackStack() }

            // Login succeeds if the browser navigates to https://mymadison.ps.jmu.edu/psp/pprd/JMU/CUST/h/?tab=DEFAULT
            private fun Uri.isLoggedIn() = (host == "mymadison.ps.jmu.edu"
                    && path == "/psp/pprd/JMU/CUST/h/"
                    && query == "tab=DEFAULT")

        }

        // Set WebView settings to load images, enable JavaScript, cache, and disable geolocation tracking.
        with(browser_view.settings) {
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            setAppCacheEnabled(true)
            setGeolocationEnabled(false)
        }
        browser_view.loadUrl(MYMADISON_LOGIN_BASE_URL)
    }
}