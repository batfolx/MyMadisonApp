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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.fragment_termselector.*

class TermSelectorFragment : Fragment() {
    private val fragmentMan: FragmentActivity = FragmentActivity()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_termselector, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // This goes to the enroll fragment after selecting the fall term for the current year.
        fall_term_button.setOnClickListener(View.OnClickListener {
            fragmentManager?.commit {
                replace(R.id.term_layout, EnrollFragment())
                .addToBackStack(null)

                makeButtonsDisappear()
            }

        })
        // This goes to the enroll fragment after selecting the spring term for the current year.
        spring_term_button.setOnClickListener(View.OnClickListener {

            fragmentManager?.commit {
                replace(R.id.term_layout, EnrollFragment())
                    .addToBackStack(null)

                makeButtonsDisappear()
            }
        })

        //This goes to the enroll fragment after selecting the summer term for the current year.
        summer_term_button.setOnClickListener(View.OnClickListener {
            fragmentManager?.commit {
                replace(R.id.term_layout, EnrollFragment())
                    .addToBackStack(null)

                makeButtonsDisappear()
            }
        })



    }

    private fun makeButtonsDisappear() {
        fall_term_button.visibility = View.GONE
        spring_term_button.visibility = View.GONE
        summer_term_button.visibility = View.GONE
    }

}