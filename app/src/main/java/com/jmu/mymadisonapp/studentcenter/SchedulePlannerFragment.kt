package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_scheduleplanner.*
import kotlinx.android.synthetic.main.fragment_studentcenter.*
import org.koin.android.ext.android.get

class SchedulePlannerFragment : Fragment()
{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         return inflater.inflate(R.layout.fragment_scheduleplanner, container, false)
         }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        schedule_planner_button.setOnClickListener {
            fragmentManager?.commit {
                replace(R.id.schedule_planner_layout, SchedulePlannerWebView()).addToBackStack(null)
            }
            makeButtonsDisappear()
        }





    }

    fun makeButtonsDisappear() {
        back_to_sc_academic_schedule_planner.visibility = View.GONE
        schedule_planner_button.visibility = View.GONE
    }





}