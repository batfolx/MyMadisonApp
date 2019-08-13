package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_academicrequirements.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector

//TODO
class AcademicRequirementsFragment : Fragment() {

    lateinit var service: MyMadisonService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_academicrequirements, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        service = get<MyMadisonService>()
        lifecycleScope.launch {
            val gradRequirements = service.getAcademicRequirements().await().body()
            MainScope().launch {
                academic_requirements_textField.text = gradRequirements?.requirements?.joinToString("\n") { "${it.genEdProgram} ${it.genEdClusterOne}" }
            }


        }
    }

}


data class ListsOfGradRequirements(
    @Selector("tr[id^=trDERIVED_SSS_SCL]")
    var requirements: List<GraduationRequirements> = emptyList()
)

data class GraduationRequirements(
    @Selector("span[id^=JAA_DP_REQ_VW_DESCR254A]")
    var genEdProgram: String = "",
    @Selector("span[id^=DERIVED_JAA_DESCR20]")
    var genEdClusterOne: String = ""



)