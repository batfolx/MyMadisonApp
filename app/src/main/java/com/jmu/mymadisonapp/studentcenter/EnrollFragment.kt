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
            jmu_text_view.text = ""
        }

        add_button.setOnClickListener {
            jmu_text_view.text = "Add button clicked"
        }

        student_center_button.setOnClickListener {


        }

    }
}