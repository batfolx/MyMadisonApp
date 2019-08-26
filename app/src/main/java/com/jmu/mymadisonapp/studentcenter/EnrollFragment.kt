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

package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.buttonNames
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.enroll_course_items.*
import kotlinx.android.synthetic.main.enroll_course_items.view.*
import kotlinx.android.synthetic.main.enroll_course_items.view.description_enroll
import kotlinx.android.synthetic.main.enroll_course_items.view.drop_button
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_enroll.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.droidsonroids.jspoon.annotation.Selector
import org.koin.android.ext.android.get
import java.text.Normalizer

/**
 * A Fragment for the enroll part of the app.
 */
class EnrollFragment : Fragment() {

    private lateinit var service: MyMadisonService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_enroll, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        service = get<MyMadisonService>()
        lifecycleScope.launch {
/*            val enrolledClasses = service.getFormattedEnrolledClasses().await().body()
            courses_recycler_view.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            courses_recycler_view.adapter = EnrollClassAdapter(enrolledClasses!!)*/

            val enrolledClasses = service.getEnrolledClasses().await().body()
            courses_recycler_view.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            courses_recycler_view.adapter = EnrollClassAdapter(enrolledClasses!!)

            MainScope().launch {


            }
        }


        add_button.setOnClickListener {
            fragmentManager?.commit {
                replace(R.id.enroll_layout, AddFragment()).addToBackStack(null)
            }

            add_button.visibility = View.GONE
        }

        student_center_button.setOnClickListener {


            fragmentManager?.commit {
                replace(R.id.enroll_layout, StudentCenterFragment()).addToBackStack(null)
            }

            makeButtonsDisappear()

        }

    }

    inner class EnrollClassAdapter(val enrolledClasses: ListOfEnrolledClasses) :
        RecyclerView.Adapter<EnrollClassAdapter.EnrollClassHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollClassHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.enroll_course_items, parent, false)
            return EnrollClassHolder(view)
        }

        override fun getItemCount(): Int {
            return enrolledClasses.listOfEnrolledClasses.size
        }

        override fun onBindViewHolder(holder: EnrollClassHolder, position: Int) {

/*            log("the holder HELEOEOEOEOEOEOEOEOEOEOEOEOEOEO", enrolledClasses.listOfEnrolledClasses.toString())
            with(holder.itemView) {
                description_enroll.text = enrolledClasses.listOfEnrolledClasses[position].className
                room_number_enroll.text = enrolledClasses.listOfEnrolledClasses[position].scheduleInfo
            }*/


            if (enrolledClasses.listOfEnrolledClasses[position].status == "NotDropped") {

                with(holder.itemView) {
                    val classNum: String =
                        "${enrolledClasses.listOfEnrolledClasses[position].classNumber}, " +
                                "credits: ${enrolledClasses.listOfEnrolledClasses[position].units} "

                    description_enroll.text =
                        enrolledClasses.listOfEnrolledClasses[position].description
                    days_and_times_enroll.text =
                        enrolledClasses.listOfEnrolledClasses[position].daysAndTimes
                    instructor_enroll.text =
                        enrolledClasses.listOfEnrolledClasses[position].instructor
                    room_number_enroll.text = enrolledClasses.listOfEnrolledClasses[position].room
                    class_number_enroll.text = classNum

                    drop_button.setOnClickListener{
                        status_enroll.text = "You have dropped this class! Position is $position"
                    }

                }


            }


        }


        inner class EnrollClassHolder(textView: View) : RecyclerView.ViewHolder(textView) {
            init {
                textView.setOnClickListener {
                }
            }
        }
    }


    private fun makeButtonsDisappear() {

        student_center_button.visibility = View.GONE
        add_button.visibility = View.GONE
    }


}


data class ListOfEnrolledClasses(
    @Selector("tr[id^=trSTDNT_ENRL_SSVW]")
    var listOfEnrolledClasses: MutableList<EnrolledClasses> = mutableListOf()
)


data class EnrolledClasses(
    @Selector("span[id^=E_CLASS_DESCR]")
    var description: String = "",

    @Selector("span[id^=DERIVED_REGFRM1_SSR_MTG_SCHED_LONG]")
    var daysAndTimes: String = "",

    @Selector("div[id^=win0divDERIVED_REGFRM1_SSR_MTG_LOC_LONG]")
    var room: String = "",

    @Selector("div[id^=win0divDERIVED_REGFRM1_SSR_INSTR_LONG]")
    var instructor: String = "",

    @Selector("span[id^=E_CLASS_NAME]")
    var classNumber: String = "",

    @Selector("span[id^=STDNT_ENRL_SSVW_UNT_TAKEN]")
    var units: String = "",

    @Selector("img[src=\"/cs/ecampus/cache27/PS_CS_STATUS_DROPPED_ICN_1.gif\"]")
    var status: String = "NotDropped"
)


data class ListOfFormattedEnrolledClasses(
    @Selector("table[id^=STDNT_WEEK_SCHD]")
    var listOfEnrolledClasses: MutableList<FormattedEnrolledClasses> = mutableListOf()
)

data class FormattedEnrolledClasses(
    @Selector("div[id^=win0divCLASS_NAME]")
    var className: String = "",

    @Selector("div[id^=win0divDERIVED_SSS_SCL_SSR_MTG_SCHED_LONG]")
    var scheduleInfo: String = ""
)