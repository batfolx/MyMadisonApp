package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.whenStarted
import com.jmu.mymadisonapp.R
import kotlinx.android.synthetic.main.fragment_academicplanner.*
import org.jsoup.Jsoup
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

//TODO
class AcademicPlannerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_academicplanner, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        academic_planner_text_view.text = ""
    }

    var url =
        "https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL?PORTALPARAM_PTCNAV=HC_SSS_STUDENT_CENTER&EOPP.SCNode=CUST&EOPP.SCPortal=JMU&EOPP.SCName=ADMN_JMU_SA_CENTER&EOPP.SCLabel=&EOPP.SCPTcname=PT_PTPP_SCFNAV_BASEPAGE_SCR&FolderPath=PORTAL_ROOT_OBJECT.PORTAL_BASE_DATA.CO_NAVIGATION_COLLECTIONS.ADMN_JMU_SA_CENTER.ADMN_S201105051031204881596842&IsFolder=false&PortalActualURL=https%3a%2f%2fmymadison.ps.jmu.edu%2fpsc%2fecampus%2fJMU%2fSPRD%2fc%2fSA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL&PortalContentURL=https%3a%2f%2fmymadison.ps.jmu.edu%2fpsc%2fecampus%2fJMU%2fSPRD%2fc%2fSA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL&PortalContentProvider=SPRD&PortalCRefLabel=Student%20Center&PortalRegistryName=JMU&PortalServletURI=https%3a%2f%2fmymadison.ps.jmu.edu%2fpsp%2fpprd%2f&PortalURI=https%3a%2f%2fmymadison.ps.jmu.edu%2fpsc%2fpprd%2f&PortalHostNode=CUST&NoCrumbs=yes&PortalKeyStruct=yes"
    val doc = Jsoup.connect(url).get()

    val jmuselector = "div"

    //response


}