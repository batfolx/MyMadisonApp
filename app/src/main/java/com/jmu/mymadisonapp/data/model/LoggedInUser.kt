/*
 * Copyright 2019 Timothy Logan
 * Copyright 2019 Victor Velea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmu.mymadisonapp.data.model

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import com.jmu.mymadisonapp.html.*
import com.jmu.mymadisonapp.moshi
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*
import kotlin.reflect.KMutableProperty0

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val eID: String,
    val displayName: String
)

class HoursEnrolledConverter : ElementConverter<Map<String, Int>> {
    override fun convert(node: Element, selector: Selector): Map<String, Int> =
        with(node.text().trim()) { substring(0, indexOf("Hours Enrolled")).trimEnd() }
            .split(Regex(": |\\s+")).filter { it.isNotBlank() }
            .chunked(2) { (name, amount) -> name to amount.toInt() }
            .toMap()
}

//Major: Computer Science - BS (GPA 2.837)<br>
//Minor: Robotics (GPA 3.105)<br>
//<br>Major/Minor GPA Last Updated: 05/24/2019

class DeclaredSubjectConverter : ElementConverter<DeclaredSubject> {
    override fun convert(node: Element, selector: Selector): DeclaredSubject =
        with(node.text().trim()
            .split(Regex("\\)\\s"))
            .filter { it.isNotBlank() }
            .groupBy({ it.substringBefore(":") }) { it.substringAfter(": ") }) {

            /*val major =
                getOrElse(0) { "Undeclared (GPA 0.000)" }
                    .let {
                        it.substring(0, it.indexOf(" (")) to
                                it.substringAfter("GPA ")
                    }
            val minor =
                getOrElse(1) { "Undeclared (GPA 0.000)" }
                    .let {
                        it.substring(0, it.indexOf(" (")) to
                                it.substringAfter("GPA ")
                    }
            val lastUpdated = SimpleDateFormat("MM/dd/yyyy").parse(getOrElse(2) { "00/00/0000" })*/
            DeclaredSubject(
                this.getOrDefault("Major", emptyList()).map {
                    Subject(
                        it.substring(0, it.indexOf(" (")),
                        it.substringAfter("GPA ").toFloat()
                    )
                },
                this.getOrDefault("Minor", emptyList()).map {
                    Subject(
                        it.substring(0, it.indexOf(" (")),
                        it.substringAfter("GPA ").toFloat()
                    )
                },
                SimpleDateFormat("MM/dd/yyyy").parse(
                    this.getOrDefault(
                        "Major/Minor GPA Last Updated",
                        emptyList()
                    ).firstOrNull() ?: "00/00/0000"
                )
            )
        }
}

data class Subject(val name: String = "", val gpa: Float = 0f)

//@Selector("div[data-role=\"content\"]")
data class StudentUndergradInfo(
    @Selector("div#SSSGROUPBOXRIGHTNBO2_WRAPPER span.PSHYPERLINKDISABLED") var holds: Int = 0,
    @Selector("div#SSSGROUPBOXRIGHTNBO3_WRAPPER span.PSHYPERLINKDISABLED") var toDos: Int = 0,
    @Selector("div#SSSGROUPBOXRIGHTNBO4_WRAPPER > span") var cumGPA: GPA = GPA(),
    @Selector("div#SSSGROUPBOXRIGHTNBO5_WRAPPER > span") var lastSemGPA: GPA = GPA(),
    @Selector(
        "div#SSSGROUPBOXRIGHTNBO6_WRAPPER > span",
        converter = HoursEnrolledConverter::class
    ) var hoursEnrolled: Map<String, Int> = emptyMap(),
    @Selector(
        "div#SSSGROUPBOXRIGHTNBO7_WRAPPER > span",
        converter = DeclaredSubjectConverter::class
    ) var subject: DeclaredSubject = DeclaredSubject()
)

data class GPA(
    @Selector("span.PSHYPERLINKDISABLED") var gpa: Float = 0f,
    @Selector("span.DASHBOARDDESCR") var type: String = ""
)

data class DeclaredSubject(
    val majors: List<Subject> = emptyList(),
    val minors: List<Subject> = emptyList(),
    val gpaLastUpdated: Date = Calendar.getInstance().time
)


object UGInfo : Adapter<StudentUndergradInfo> {

    private fun Builder<StudentUndergradInfo>.gpaProp(
        value: String,
        ref: StudentUndergradInfo.() -> KMutableProperty0<GPA>
    ) =
        property(value, ref) {
            this.floatProperty("span.PSHYPERLINKDISABLED") { ::gpa }
            this.stringProperty("span.DASHBOARDDESCR") { ::type }
        }

    override fun build(): Builder<StudentUndergradInfo> = selector {
        intProperty("div#SSSGROUPBOXRIGHTNBO2_WRAPPER span.PSHYPERLINKDISABLED") { ::holds }
        intProperty("div#SSSGROUPBOXRIGHTNBO3_WRAPPER span.PSHYPERLINKDISABLED") { ::toDos }
        gpaProp("div#SSSGROUPBOXRIGHTNBO4_WRAPPER > span") { ::cumGPA }
        gpaProp("div#SSSGROUPBOXRIGHTNBO5_WRAPPER > span") { ::lastSemGPA }
        property("div#SSSGROUPBOXRIGHTNBO6_WRAPPER > span", { ::hoursEnrolled }) {
            defaultConverter {
                split(Regex(": |\\s+")).filter { it.isNotBlank() }
                    .chunked(2) { (name, amount) -> name to amount.toInt() }
                    .toMap()
            }
            attr {
                with(it.text().trim()) { substring(0, indexOf("Hours Enrolled")).trimEnd() }
            }
        }
        property("div#SSSGROUPBOXRIGHTNBO7_WRAPPER > span", { ::subject }) {
            attr { it.text().trim() }
            defaultConverter {
                with(split(Regex("\\)\\s"))
                    .filter { it.isNotBlank() }
                    .groupBy({ it.substringBefore(":") }) { it.substringAfter(": ") }) {
                    DeclaredSubject(
                        this.getOrDefault("Major", emptyList()).map {
                            Subject(
                                it.substring(0, it.indexOf(" (")),
                                it.substringAfter("GPA ").toFloat()
                            )
                        },
                        this.getOrDefault("Minor", emptyList()).map {
                            Subject(
                                it.substring(0, it.indexOf(" (")),
                                it.substringAfter("GPA ").toFloat()
                            )
                        },
                        SimpleDateFormat("MM/dd/yyyy").parse(
                            this.getOrDefault(
                                "Major/Minor GPA Last Updated",
                                emptyList()
                            ).firstOrNull() ?: "00/00/0000"
                        )
                    )
                }
            }
        }
    }
}

class SAMLResponseConverter : ElementConverter<String> {
    override fun convert(node: Element, selector: Selector): String = node.`val`()
}

data class SAMLResponse(
    @Selector(
        "form[method=\"post\"] > input[name=\"SAMLResponse\"]",
        converter = SAMLResponseConverter::class
    )
    var value: String = ""
)

object SAMLResponseAdapter : Adapter<SAMLResponse> {
    override fun build(): Builder<SAMLResponse> = selector {
        stringProperty("form[method=\"post\"] > input[name=\"SAMLResponse\"]") { ::value }.attr { it.`val`() }
    }
}

class CanvasProfileConverter : ElementConverter<CanvasProfileInfo> {
    override fun convert(node: Element, selector: Selector): CanvasProfileInfo =
        moshi.adapter(CanvasProfileInfo::class.java)
            .fromJson(
                node.data().run {
                    val start = "\"current_user\":".let { indexOf(it) + it.length }
                    substring(start, indexOf("}", start) + 1)
                }
            ) ?: CanvasProfileInfo("Unknown", "")
}

data class CanvasProfileInfo(
    var display_name: String = "",
    var avatar_image_url: String = ""
)

data class CurrentUser(
    @Selector("div#application > script", converter = CanvasProfileConverter::class)
    var current_user: CanvasProfileInfo = CanvasProfileInfo()
)

object CurrentUserAdapter : Adapter<CurrentUser> {
    override fun build(): Builder<CurrentUser> = selector {
        property("div#application > script", { ::current_user }) {
            attr {
                with(it.data()) {
                    val start = "\"current_user\":".let { name -> indexOf(name) + name.length }
                    substring(start, indexOf("}", start) + 1)
                }
            }
            defaultConverter {
                moshi.adapter(CanvasProfileInfo::class.java).fromJson(this)
                    ?: CanvasProfileInfo("Unknown", "")
            }
        }
    }
}

class TermPostDataConverter : ElementConverter<Map<String, String>> {
    override fun convert(node: Element, selector: Selector): Map<String, String>? =
        node.select("input")?.associate { it.attr("id") to it.`val`() } ?: emptyMap()
}

@Selector("div#ptifrmcontent > div#ptifrmtarget iframe#ptifrmtgtframe html.chrome > body.PSPAGE")
data class GradeTerms(
    @Selector(
        "div#win0divPSHIDDENFIELDS",
        converter = TermPostDataConverter::class
    ) var termPostData: List<Pair<String, String>> = emptyList(),
    @Selector("table.PSLEVEL2GRID tr[id^=trSSR_DUMMY_RECV1$0_row]") var terms: List<Term> = emptyList()
)

data class Term(
    @Selector("div[id^=win0divTERM_CAR] > span.PSEDITBOX_DISPONLY") var term: String = "",
    @Selector("div[id^=win0divCAREER] > span.PSEDITBOX_DISPONLY") var career: String = "",
    @Selector("div[id^=win0divINSTITUTION] > span.PSEDITBOX_DISPONLY") var institution: String = ""
)

@Selector("form#SSR_SSENRL_GRADE > div.ps_pagecontainer > table.PSPAGECONTAINER > tbody")
data class ClassGrades(
    @Selector("tr div#win0divDERIVED_SSS_GRD_GROUPBOX2 table#ACE_DERIVED_SSS_GRD_GROUPBOX2 tr[id^=trTERM_CLASSES$0_row]") var grades: List<Grade> = emptyList(),
    @Selector(
        "tr div#win0divDERIVED_SSS_GRD_GROUPBOX4 table#ACE_DERIVED_SSS_GRD_GROUPBOX4 tr[id=trTERM_STATS$0_row14] div[id=win0divSTATS_TERM$13] > span[id=STATS_TERM$13]",
        defValue = "0.0"
    ) var semesterGPA: Float = 0f,
    @Selector("tr div#win0divDERIVED_SSS_GRD_GROUPBOX4 table#ACE_DERIVED_SSS_GRD_GROUPBOX4 span#ACAD_STACTN_TBL_DESCRFORMAL") var academicStanding: String = ""
)

/*object GradeTermsAdapter : Adapter<GradeTerms> {
    override fun build(): Builder<GradeTerms> = selector("div#ptifrmcontent > div#ptifrmtarget iframe#ptifrmtgtframe html.chrome > body.PSPAGE") {
        collection("div#win0divPSHIDDENFIELDS input", { ::termPostData }) {
            itemMapper { (this.attr("id") to this.`val`()) }
            itemMapper("", ) { }
            collectionMapper { this }
        }
        collection("table.PSLEVEL2GRID tr[id^=trSSR_DUMMY_RECV1$0_row]", { ::terms }) {
            collectionMapper {  }
            itemMapper {  }

        }
    }
}*/

class FloatConverter : ElementConverter<Float> {
    override fun convert(node: Element, selector: Selector): Float =
        node.text().toFloatOrNull() ?: 0f
}

data class Grade(
    @Selector("div[id^=win0divCLS_LINK] a[id^=CLS_LINK]") var className: String = "",
    @Selector("div[id^=win0divCLASS_TBL_VW_DESCR]") var description: String = "",
    @Selector("div[id^=win0divSTDNT_ENRL_SSV1_UNT_TAKEN]", converter = FloatConverter::class) var units: Float = 0f,
    @Selector("div[id^=win0divGRADING_BASIS]") var grading: String = "",
    @Selector("div[id^=win0divSTDNT_ENRL_SSV1_CRSE_GRADE_OFF]") var grade: String = "",
    @Selector(
        "div[id^=win0divSTDNT_ENRL_SSV1_GRADE_POINTS]",
        converter = FloatConverter::class
    ) var gradePoints: Float = 0f
)

@Selector("form#SSR_SSENRL_GRADE > div.ps_pagecontainer > table.PSPAGECONTAINER > tbody div[id=win0divSTDNT_ENRL_SSV2$0] > table > tbody")
data class ClassSchedule(
    @Selector("div[id^=win0divDERIVED_REGFRM1_DESCR20] > table > tbody") var schedule: List<DetailedClass> = emptyList()
)

data class DetailedClass(
    @Selector("td.PAGROUPDIVIDER") var title: String = "",
    @Selector("table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=STATUS$0]") var status: String = "",
    @Selector("table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=ENRLSTATUSREASON$0]") var reason: String = "",
    @Selector(
        "table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=DERIVED_REGFRM1_UNT_TAKEN$0]",
        defValue = "0.0"
    ) var units: Float = 0f,
    @Selector("table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=GB_DESCR$0]") var grading: String = "",
    @Selector("table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=DERIVED_REGFRM1_CRSE_GRADE_OFF$0]") var grade: String = "",
    @Selector(
        "table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divDERIVED_CLS_DTL_CLASS_NBR$0] > span",
        defValue = "0"
    ) var classNumber: Int = 0,
    @Selector(
        "table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divMTG_SECTION$0] > span",
        defValue = "0"
    ) var section: Int = 0,
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divMTG_COMP$0] > span") var component: String = "",
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_SCHED] > span") var datesAndTimes: List<String> = emptyList(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_LOC] > span") var room: List<String> = emptyList(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divDERIVED_CLS_DTL_SSR_INSTR_LONG] > span") var instructor: List<String> = emptyList(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_DATES] > span") var startEndDate: List<String> = emptyList(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divMTG_AID$0] > span") var aidEligible: String = ""
)