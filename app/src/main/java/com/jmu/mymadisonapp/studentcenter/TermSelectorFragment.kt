package com.jmu.mymadisonapp.studentcenter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*

import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.fragment_termselector.*
import kotlinx.coroutines.withTimeoutOrNull

class TermSelectorFragment : Fragment() {
    private val fragmentMan: FragmentActivity = FragmentActivity()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_termselector, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // This goes to the enroll fragment after selecting the fall term for the current year.
        fall_term_button.setOnClickListener(View.OnClickListener {
            fragmentManager?.commit {
                replace(R.id.term_layout, EnrollFragment())
                .addToBackStack(null)

                makeButtonsDisappear()
            }

        })
        // This goes to the enroll fragment after selecting the spring term for the current year.
        spring_term_button.setOnClickListener(View.OnClickListener {

            fragmentManager?.commit {
                replace(R.id.term_layout, EnrollFragment())
                    .addToBackStack(null)

                makeButtonsDisappear()
            }
        })

        //This goes to the enroll fragment after selecting the summer term for the current year.
        summer_term_button.setOnClickListener(View.OnClickListener {
            fragmentManager?.commit {
                replace(R.id.term_layout, EnrollFragment())
                    .addToBackStack(null)

                makeButtonsDisappear()
            }
        })



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun makeButtonsDisappear() {
        fall_term_button.visibility = View.GONE
        spring_term_button.visibility = View.GONE
        summer_term_button.visibility = View.GONE
    }

}