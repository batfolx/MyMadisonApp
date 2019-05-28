package com.jmu.mymadisonapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class EnrollFragment : Fragment()
{
    var drop_button: Button?= view?.findViewById(R.id.drop_button)
    var add_button: Button? = view?.findViewById(R.id.add_button)
    var edit_button: Button? = view?.findViewById(R.id.edit_button)
    var swap_button: Button? = view?.findViewById(R.id.swap_button)



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view: View = inflater.inflate(R.layout.fragment_enroll, container, false)
        return view
    }








}