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

package com.jmu.mymadisonapp.ui.home

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.ui.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.text_home
import kotlinx.android.synthetic.main.undergraduate_dashboard.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false).apply {
        homeViewModel.text.observe(this@HomeFragment, Observer {
            text_home.text = it
        })

        // Observe the result of getting the Undergraduate Dashboard information to display to the user
        mainViewModel.studentInfoLiveData.observe(this@HomeFragment, Observer {
            holds_text.text = "${it.holds}\nHolds"
            to_dos_text.text = "${it.toDos}\nTo Dos"
            cum_gpa_text.text = "${it.cumulativeGPA}\nCumulative GPA"
            last_sem_gpa_text.text = "${it.lastSemesterGPA}\nLast Semester GPA"
            hours_enrolled_text.text =
                "${it.hoursEnrolled.joinToString("\n") { (name, amount) -> "$name: $amount" }}\nHours Enrolled"
            major_minor_text.text =
                """
                    ${it.majors.joinToString("\nMajor: ", "Major: ") { major -> "${major.name} (GPA ${major.gpa})" }}
                    ${it.minors.joinToString("\nMinor: ", "Minor: ") { minor -> "${minor.name} (GPA ${minor.gpa})" }}
                    Major/Minor GPA Last Updated: ${SimpleDateFormat("MM/dd/yyyy").format(it.gpaLastUpdated)}
                    """.trimIndent()
        })
    }
}