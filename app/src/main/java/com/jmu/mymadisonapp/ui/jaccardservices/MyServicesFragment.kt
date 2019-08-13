package com.jmu.mymadisonapp.ui.jaccardservices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_myservices.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector


class MyServicesFragment : Fragment()
{

    lateinit var service: MyMadisonService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_myservices, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        service = get()
        lifecycleScope.launch {
            val csInfo = service.getCardServicesInformation().await().body()
            log("This is the fucking card services info $csInfo")
            MainScope().launch {
                my_services_text_view.text = csInfo?.flexBalance
            }
        }
    }


}


data class CardServicesInformation(
    @Selector("strong")
    var flexBalance: String = ""
)