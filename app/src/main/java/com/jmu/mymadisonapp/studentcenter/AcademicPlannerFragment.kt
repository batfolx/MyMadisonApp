package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_academicplanner.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector
import org.koin.android.ext.android.get


//TODO
class AcademicPlannerFragment : Fragment() {

    lateinit var service: MyMadisonService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_academicplanner, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        service = get()

        lifecycleScope.launch {

            val courseCatalogs = service.getCourseCatalogs().await().body()

            MainScope().launch {
                academic_planner_text_view.text = courseCatalogs?.subjectCodes?.joinToString("\n") {
                    it.subjectCode
                }
            }
        }

    }


}


data class ListOfAcademicCourseCatalogs(

    @Selector("table[id^=ACE_DERIVED_SSS_BCC_]")
    var subjectCodes: List<AcademicCourseCatalog> = emptyList()
)

data class AcademicCourseCatalog(
    @Selector("span[id^=DERIVED_SSS_BCC_GROUP_BOX_1]")
    var subjectCode: String = ""

)

