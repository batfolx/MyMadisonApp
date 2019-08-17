package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.schedule_planner_web_view.*

class SchedulePlannerWebView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.schedule_planner_web_view, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        schedule_planner_web_view.webViewClient = WebViewClient()

        with(schedule_planner_web_view.settings){
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            setAppCacheEnabled(true)
            setGeolocationEnabled(false)
            setSupportZoom(true)
            textZoom = 100
            displayZoomControls = true
            builtInZoomControls = true

        }
        schedule_planner_web_view.loadUrl("https://jmu.collegescheduler.com")
    }
}