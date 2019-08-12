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
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.navigation.Navigation
import com.google.android.material.internal.NavigationMenu
import com.jmu.mymadisonapp.MainActivity
import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.fragment_studentcenter.*

class StudentCenterFragment : Fragment() {
    var supportFrag = FragmentActivity()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_studentcenter, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        academics_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

                student_center_textField.text = "You didnt slect anything"


            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position) {
                    //Search is selected

                    0 -> student_center_textField.text = "You selected search!"
                    1 -> {
                        fragmentManager?.commit {
                            replace(R.id.student_center_layout, TermSelectorFragment())
                                .addToBackStack(null)
                        }
                    }//Enroll is selected
                    2 -> {
                        fragmentManager?.commit {
                            replace(
                                R.id.student_center_layout,
                                SchedulePlannerFragment()
                            )
                            addToBackStack(null)
                        }
                    }
                    3 -> {
                        fragmentManager?.commit {
                            replace(
                                R.id.student_center_layout,
                                AcademicPlannerFragment()
                            ) //academic Planner is selected
                            addToBackStack(null)
                        }
                    }

                    4 -> {

                        fragmentManager?.commit {
                            replace(R.id.student_center_layout, AcademicRequirementsFragment())
                            addToBackStack(null)
                        }
                    } //Academic requirements is selected


                }


            }
        }

    }


}