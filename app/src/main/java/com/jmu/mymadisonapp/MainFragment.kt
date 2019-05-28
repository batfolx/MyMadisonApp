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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

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

        fragmentManager?.addOnBackStackChangedListener(this::onLoggedIn)

        lifecycleScope.launch {
            if (!mainViewModel.isLoggedIn())
                MainScope().launch {
                    fragmentManager?.commit {
                        add(R.id.content_container, BrowserFragment())
                        addToBackStack(null)
                    }
                }
            else MainScope().launch { onLoggedIn() }
        }
        mainViewModel.undergradInfoLiveData.observe(this, Observer {
            holds_text.text = "${it.holds}\nHolds"
            to_dos_text.text = "${it.toDos}\nTo Dos"
            cum_gpa_text.text = "${it.cumGPA.gpa}\n${it.cumGPA.type}"
            last_sem_gpa_text.text = "${it.lastSemGPA.gpa}\n${it.lastSemGPA.type}"
            hours_enrolled_text.text =
                "${it.hoursEnrolled.entries.joinToString("\n") { (name, amount) -> "$name: $amount" }}\nHours Enrolled"
            major_minor_text.text =
                "Major: ${it.subject.major} (GPA ${it.subject.majorGPA})\nMinor: ${it.subject.minor} (GPA ${it.subject.minorGPA}\nMajor/Minor GPA Last Updated: ${SimpleDateFormat(
                    "MM/dd/yyyy"
                ).format(it.subject.gpaLastUpdated)}"
        })
    }

    private fun onLoggedIn() {
        mainViewModel.getUndergraduateDashboard()
    }


}
