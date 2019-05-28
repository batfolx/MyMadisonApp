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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_browser.*

/**
 * The default activity to load upon startup.
 */
class MainActivity : AppCompatActivity() {

	private lateinit var appBarConfiguration: AppBarConfiguration

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		// Set the toolbar to display navigation
		setSupportActionBar(toolbar)

		// Check if permissions have been granted
		checkPermissions()

		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
				R.id.nav_tools, R.id.nav_share, R.id.nav_send), drawer_layout)

		findNavController(R.id.nav_host_fragment).apply {
			setupActionBarWithNavController(this, appBarConfiguration)
			nav_view.setupWithNavController(this)
		}
	}

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
