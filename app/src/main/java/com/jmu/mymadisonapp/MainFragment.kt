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

package com.jmu.mymadisonapp


import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.jmu.mymadisonapp.ui.MainViewModel
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.undergraduate_dashboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * The main [Fragment] to manage navigation and determine if the user must login.
 */
class MainFragment : Fragment() {

    // Inject the ViewModel into this Fragment
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.app_bar_main, container, false).apply {
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Notify that the user is logged in on return from the BrowserFragment
        fragmentManager?.addOnBackStackChangedListener {
            if (fragmentManager?.backStackEntryCount == 0)
                onLoggedIn()
        }

        // Launch in a background coroutine keeping with lifecycle states to cancel the process if the user exits the app
        lifecycleScope.launch(lifecycleScope.coroutineContext + Dispatchers.IO) {
            // If not logged in, open the login browser, otherwise update UI
            if (!mainViewModel.isLoggedIn())
                MainScope().launch {
                    fragmentManager?.commit {
                        add(R.id.content_container, BrowserFragment())
                        addToBackStack(null)
                    }
                }
            else MainScope().launch { onLoggedIn() }
        }

        // Observe the result of getting the Undergraduate Dashboard information to display to the user
        mainViewModel.undergradInfoLiveData.observe(this, Observer {
            holds_text.text = "${it.holds}\nHolds"
            to_dos_text.text = "${it.toDos}\nTo Dos"
            cum_gpa_text.text = "${it.cumGPA.gpa}\n${it.cumGPA.type}"
            last_sem_gpa_text.text = "${it.lastSemGPA.gpa}\n${it.lastSemGPA.type}"
            hours_enrolled_text.text =
                "${it.hoursEnrolled.entries.joinToString("\n") { (name, amount) -> "$name: $amount" }}\nHours Enrolled"
            major_minor_text.text =
                "Major: ${it.subject.major} (GPA ${it.subject.majorGPA})\nMinor: ${it.subject.minor} (GPA ${it.subject.minorGPA})\nMajor/Minor GPA Last Updated: ${SimpleDateFormat(
                    "MM/dd/yyyy"
                ).format(it.subject.gpaLastUpdated)}"
        })
    }

    private fun onLoggedIn() = mainViewModel.onLoggedIn()


}
