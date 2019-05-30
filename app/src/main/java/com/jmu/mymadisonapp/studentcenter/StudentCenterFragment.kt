package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.room.model.Student
import kotlinx.android.synthetic.main.fragment_studentcenter.*
import java.lang.reflect.Field

class StudentCenterFragment : Fragment()
{
    var supportFrag = FragmentActivity()
    lateinit var childSupportFrag: Field

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                   return inflater.inflate(R.layout.fragment_studentcenter, container, false)
        }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        academics_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

                student_center_textField.text="You didnt slect anything"


            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                if (position == 0) //Search is selected
                {
                    student_center_textField.  text="You selected search!"

                }
                else if (position == 1) //Enroll is selected
                {

                    fragmentManager?.beginTransaction()?.replace(R.id.student_center_layout, TermSelectorFragment())?.commit()


                }
                else if (position == 2) //schedule planner is selected
                {
                    fragmentManager?.commit {
                        replace(R.id.student_center_layout, SchedulePlannerFragment()).remove(StudentCenterFragment())
                        addToBackStack(null)

                    }
                }

                else if (position == 3) //academic Planner is selected
                {
                    fragmentManager?.commit {
                        replace(R.id.student_center_layout, AcademicPlannerFragment())
                        addToBackStack(null)
                    }
                }
                else if (position == 4) //Academic requirements is selected
                {
                    fragmentManager?.commit {
                        replace(R.id.student_center_layout, AcademicRequirementsFragment())
                        addToBackStack(null)
                    }
                }
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}