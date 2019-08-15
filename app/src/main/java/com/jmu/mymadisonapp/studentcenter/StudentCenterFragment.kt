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
import com.jmu.mymadisonapp.room.model.Student
import com.jmu.mymadisonapp.studentcenter.search.SearchFragment
import kotlinx.android.synthetic.main.fragment_studentcenter.*
import java.lang.reflect.Field

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

                    1 -> {
                        fragmentManager?.commit {
                            replace(R.id.student_center_layout, SearchFragment()).addToBackStack(null)
                        }
                    }
                    2 -> {
                        fragmentManager?.commit {
                            replace(R.id.student_center_layout, TermSelectorFragment())
                                .addToBackStack(null)
                        }
                    }//Enroll is selected
                    3 -> {
                        fragmentManager?.commit {
                            replace(
                                R.id.student_center_layout,
                                SchedulePlannerFragment()
                            )
                            addToBackStack(null)
                        }
                    }
                    4 -> {
                        fragmentManager?.commit {
                            replace(
                                R.id.student_center_layout,
                                AcademicPlannerFragment()
                            ) //academic Planner is selected
                            addToBackStack(null)
                        }
                    }

                    5 -> {

                        fragmentManager?.commit {
                            replace(R.id.student_center_layout, AcademicRequirementsFragment())
                            addToBackStack(null)
                        }
                    } //Academic requirements is selected


                }


            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}