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
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_class_schedule.*
import kotlinx.android.synthetic.main.fragment_enroll.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.droidsonroids.jspoon.annotation.Selector
import org.koin.android.ext.android.get

/**
 * A Fragment for the enroll part of the app.
 */
class EnrollFragment : Fragment()
{

    private lateinit var service: MyMadisonService


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_enroll, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        service = get<MyMadisonService>()
        lifecycleScope.launch{
            val enrolledClasses = service.getEnrolledClasses().await().body()

            MainScope().launch {
                jmu_text_view.text = enrolledClasses?.listOfEnrolledClasses?.joinToString("\n") {
                    "${it.description}, ${it.daysAndTimes}, ${it.room}, ${it.instructor}. "
                }
            }
        }



        drop_button.setOnClickListener {
            jmu_text_view.text = "Drop button clicked"
        }

        add_button.setOnClickListener {
            jmu_text_view.text = "Add button clicked"
        }

        edit_button.setOnClickListener {
            jmu_text_view.text = "Edit button clicked"
        }

        swap_button.setOnClickListener {
            jmu_text_view.text = "Swap button clicked"
        }

        student_center_button.setOnClickListener {

            jmu_text_view.text = "Student center button clicked"

            fragmentManager?.commit {
                replace(R.id.enroll_layout, StudentCenterFragment()).addToBackStack(null)
            }

            makeButtonsDisappear()

        }

    }


    private fun makeButtonsDisappear()
    {
        drop_button.visibility = View.GONE
        add_button.visibility = View.GONE
        edit_button.visibility = View.GONE
        swap_button.visibility = View.GONE
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
    var instructor: String = ""
)

class ClassesRecyclerAdapter(var itemView: View) : RecyclerView.ViewHolder(itemView)
{

    init {

    }
}