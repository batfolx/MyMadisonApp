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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jmu.mymadisonapp.ui.MainViewModel
import com.jmu.mymadisonapp.ui.grades.GradesViewModel
import com.jmu.mymadisonapp.ui.slideshow.ClassScheduleViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_browser.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * The default activity to load upon startup.
 */
class MainActivity : AppCompatActivity() {

	private lateinit var appBarConfiguration: AppBarConfiguration
	private val mainViewModel: MainViewModel by viewModel()
	private val gradesViewModel: GradesViewModel by viewModel()
	private val schedulesViewModel: ClassScheduleViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		// Set the toolbar to display navigation
		setSupportActionBar(toolbar)

		// Check if permissions have been granted
		checkPermissions()

		// Notify that the user is logged in on return from the BrowserFragment
		supportFragmentManager.addOnBackStackChangedListener {
			if (supportFragmentManager.backStackEntryCount == 0)
				onLoggedIn()
		}

		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.nav_home, R.id.nav_grades, R.id.nav_course,
				R.id.nav_tools, R.id.nav_share, R.id.nav_send, R.id.student_center), drawer_layout)

		findNavController(R.id.nav_host_fragment).apply {
			setupActionBarWithNavController(this, appBarConfiguration)
			nav_view.setupWithNavController(this)
		}
		mainViewModel.studentInfoLiveData.observe(this, Observer {
			log("StudentInfo", "New value: $it")
			GlideApp.with(this)
				.load(it.avatar)
				.skipMemoryCache(true)
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
				.circleCrop()
				.into(user_avatar)
			display_name.text = it.displayName
			eID.text = it.eID
		})
		gradesViewModel.gradesLiveData.observe(this, Observer {
			log("TermData", "Content: ${it.joinToString("\n")}")
		})
		schedulesViewModel.classSchedulesLiveData.observe(this, Observer {
			log("Schedule", "Content: ${it.joinToString("\n")}")
		})
		fab.setOnClickListener {
			supportFragmentManager.fragments.find { it is BrowserFragment }
				?.let { (it as BrowserFragment).browser_view.zoomOut() }
		}


		// Launch in a background coroutine keeping with lifecycle states to cancel the process if the user exits the app
		lifecycleScope.launch(lifecycleScope.coroutineContext + Dispatchers.IO) {
			// If not logged in, open the login browser, otherwise update UI
			if (!mainViewModel.isLoggedIn())
				MainScope().launch {
					supportFragmentManager.commit {
						replace(R.id.content_container, BrowserFragment())
						addToBackStack(null)
					}
				}
			else MainScope().launch { onLoggedIn() }
		}
	}

	private fun onLoggedIn() =
		mainViewModel.onLoggedIn().also { gradesViewModel.getAllGrades(); schedulesViewModel.getAllClassSchedules() }

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onSupportNavigateUp(): Boolean {
		val navController = findNavController(R.id.nav_host_fragment)
		return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			111, 300 -> recreate()
		}
	}

	override fun onBackPressed() {
		when {
			supportFragmentManager.backStackEntryCount == 1 && browser_view.canGoBack() -> browser_view.goBack()
			else -> super.onBackPressed()
		}
	}
}
