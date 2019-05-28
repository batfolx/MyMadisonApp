package com.jmu.mymadisonapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.fragment_enroll.*
import kotlinx.android.synthetic.main.fragment_studentcenter.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class StudentCenterFragment : Fragment()
{
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



                if (position == 0)
                {
                    student_center_textField.  text="You selected search!"

                }
                else if (position == 1)
                {

                    fragmentManager?.commit {
                      replace(R.id.student_center_layout, EnrollFragment())
                      addToBackStack(null)
                    }


                    student_center_textField.text="You selected enroll."
                }


            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}