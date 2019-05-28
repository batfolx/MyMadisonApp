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
import com.jmu.mymadisonapp.data.MYMADISON_LOGIN_BASE_URL
import kotlinx.android.synthetic.main.fragment_browser.*


/**
 * A simple [Fragment] subclass.
 */
class BrowserFragment : Fragment() {

//    private val videoViewModel: VideoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_browser, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(CookieManager.getInstance()) {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(browser_view, true)
        }

        browser_view.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.onLoggedIn()
            }

            //            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean =
//                url?.let { Uri.parse(it) }?.shouldOverride() ?: super.shouldOverrideUrlLoading(view, url)
//
//            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
//                request?.url?.shouldOverride() ?: super.shouldOverrideUrlLoading(view, request)
            private fun String.onLoggedIn() = Uri.parse(this).isLoggedIn().ifDo { fragmentManager?.popBackStack() }

            private fun Uri.isLoggedIn() = (host == "mymadison.ps.jmu.edu"
                    && path == "/psp/pprd/JMU/CUST/h/"
                    && query == "tab=DEFAULT")

        }

        with(browser_view.settings) {
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            setAppCacheEnabled(true)
            setGeolocationEnabled(false)
        }
        browser_view.loadUrl(MYMADISON_LOGIN_BASE_URL)
    }
}