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

        inflater.inflate(R.layout.fragment_enroll, container, false).apply {


        }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drop_button.setOnClickListener(View.OnClickListener
        {
            jmu_text_view.text = ""
        })

        add_button.setOnClickListener(View.OnClickListener
        {
            jmu_text_view.text = "Add button clicked"
        })

        student_center_button.setOnClickListener(View.OnClickListener {


        })

    }
}