package com.jmu.mymadisonapp.ui.jaccardservices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.fragment_card_services_browser.*


class CardServicesBrowserFragment : Fragment()
{

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_services_browser, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        webview_card_services.webViewClient = WebViewClient()

        with(webview_card_services.settings){
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            setAppCacheEnabled(true)
            setGeolocationEnabled(false)
            setSupportZoom(true)
            textZoom = 100
            displayZoomControls = true
            builtInZoomControls = true

        }
        webview_card_services.loadUrl("https://mymadison.ps.jmu.edu/psc/pprd/JMU/CUST/s/WEBLIB_JMU_JSAT.JMU_JSATECH.FieldFormula.IScript_SendJSATech")


    }

}