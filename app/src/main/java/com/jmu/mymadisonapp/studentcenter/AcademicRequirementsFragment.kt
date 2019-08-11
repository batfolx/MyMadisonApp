package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.fragment_academicrequirements.*
import pl.droidsonroids.jspoon.annotation.Selector

//TODO
class AcademicRequirementsFragment : Fragment()
{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_academicrequirements, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val gradRequirements = GraduationRequirements()


        academic_requirements_textField.text = "General Education Program (Catalog Year 2015-16) ${gradRequirements.genEdProgram}\n -  General Education:  Cluster One ${gradRequirements.genEdCluserOne}"

    }

    fun getGraduationRequirements()
    {

    }

}


data class GraduationRequirements(
    @Selector("#DERIVED_JAA_DESCR20\\\$0")
    var genEdProgram: String = "",
    @Selector("#DERIVED_JAA_DESCR20\$1")
    var genEdCluserOne: String = "",
    @Selector("#DERIVED_JAA_DESCR20\\\$1")
    var genEdClusterTwo: String = ""
)