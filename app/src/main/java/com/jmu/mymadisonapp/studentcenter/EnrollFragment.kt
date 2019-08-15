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

import android.content.ClipData
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.leanback.widget.Presenter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.course_item.view.*
import kotlinx.android.synthetic.main.enroll_course_items.*
import kotlinx.android.synthetic.main.enroll_course_items.view.*
import kotlinx.android.synthetic.main.enroll_course_items.view.description_enroll
import kotlinx.android.synthetic.main.fragment_class_schedule.*
import kotlinx.android.synthetic.main.fragment_enroll.*
import kotlinx.android.synthetic.main.fragment_enroll.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.droidsonroids.jspoon.annotation.Selector
import org.koin.android.ext.android.get
import org.w3c.dom.Text
public var buttonNames: Array<String> = arrayOf("Edit", "Drop")
/**
 * A Fragment for the enroll part of the app.
 */
class EnrollFragment : Fragment() {

    private lateinit var service: MyMadisonService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_enroll, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        service = get<MyMadisonService>()
        lifecycleScope.launch {
            val enrolledClasses = service.getEnrolledClasses().await().body()
            courses_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            courses_recycler_view.adapter = EnrollClassAdapter(enrolledClasses!!)

            MainScope().launch {



                // courses_text_view.text = enrolledClasses?.listOfEnrolledClasses?.joinToString("\n") {
                //    "${it.description}, ${it.daysAndTimes}, ${it.room}, ${it.instructor}. "
                // }
            }
        }


        add_button.setOnClickListener {
            fragmentManager?.commit {
                replace(R.id.enroll_layout, AddEnrollFragment()).addToBackStack(null)
            }
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

        /**
         * Function that adds buttons to each of the items in the RecyclerView
         *
         */
        fun addButtonsToCourses(linearLayoutParams: LinearLayout.LayoutParams,
                                linearLayout: LinearLayout,
                                buttonNames: Array<String>,
                                holder: EnrollClassHolder
                                ) {

            //This makes it less verbose
            with(holder.itemView) {

                //iterates over the names in buttonNames, Edit, Swap, Drop and Add
                for (name in buttonNames) {

                    //creates a temporary button
                    val tmpButton = Button(context)
                    tmpButton.text = name

                    //if the name is Drop, assign a specific action listener for that button.
                    when (name) {
                        "Drop" -> tmpButton.setOnClickListener {

                            description_enroll.text = "Description changed when the name is Drop!"
                        }

                        "Edit" -> tmpButton.setOnClickListener {
                            description_enroll.text =
                                "Description changed when the name is Edit!"
                        }

                        else -> tmpButton.setOnClickListener {
                            description_enroll.text = "Description changed when the name is Else!"
                        }
                    }
                    linearLayout.addView(tmpButton, linearLayoutParams)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollClassHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.enroll_course_items, parent, false)
            return EnrollClassHolder(view)
        }

        override fun getItemCount(): Int {
            return enrolledClasses.listOfEnrolledClasses.size
        }

        override fun onBindViewHolder(holder: EnrollClassHolder, position: Int) {

            with(holder.itemView) {
                val classNum: String = "${enrolledClasses.listOfEnrolledClasses[position].classNumber}, " +
                        "credits: ${enrolledClasses.listOfEnrolledClasses[position].units} "


                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val linearLayout = findViewById<LinearLayout>(R.id.enroll_course_layout)

                addButtonsToCourses(params, linearLayout, buttonNames, holder)

                description_enroll.text = enrolledClasses.listOfEnrolledClasses[position].description
                days_and_times_enroll.text = enrolledClasses.listOfEnrolledClasses[position].daysAndTimes
                instructor_enroll.text = enrolledClasses.listOfEnrolledClasses[position].instructor
                room_number_enroll.text = enrolledClasses.listOfEnrolledClasses[position].room
                class_number_enroll.text = classNum

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
    }



}




data class ListOfEnrolledClasses(
    @Selector("tr[id^=trSTDNT_ENRL_SSVW]")
    var listOfEnrolledClasses: List<EnrolledClasses> = emptyList()
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
    var units: String = ""
)
