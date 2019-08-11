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
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.fragment_enroll.*


class EnrollFragment : Fragment()
{

 /*   var add_button: Button? = view?.findViewById(R.id.add_button)
    var edit_button: Button? = view?.findViewById(R.id.edit_button)
    var swap_button: Button? = view?.findViewById(R.id.swap_button) */



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_enroll, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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