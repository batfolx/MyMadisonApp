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
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.moshi
import com.squareup.moshi.JsonDataException
import com.timmahh.ksoup.KSoup
import com.timmahh.ksoup.ParseBuilder
import com.timmahh.ksoup.SimpleParser
import org.jsoup.nodes.Element
import java.util.*
import kotlin.reflect.KProperty1

/*class HoursEnrolledConverter : ElementConverter<Map<String, Int>> {
    override fun convert(node: Element, selector: Selector): Map<String, Int> =
        with(node.text().trim()) { substring(0, indexOf("Hours Enrolled")).trimEnd() }
            .split(Regex(": |\\s+")).filter { it.isNotBlank() }
            .chunked(2) { (name, amount) -> name to amount.toInt() }
            .toMap()
}*/

/*class DeclaredSubjectConverter : ElementConverter<DeclaredSubject> {
    override fun convert(node: Element, selector: Selector): DeclaredSubject =
        with(node.text().trim()
            .split(Regex("\\)\\s"))
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
}*/

//@Selector("div[data-role=\"content\"]")
/*data class StudentUndergradInfo(
    @Selector("div#SSSGROUPBOXRIGHTNBO2_WRAPPER span.PSHYPERLINKDISABLED") var holds: Int = 0,
    @Selector("div#SSSGROUPBOXRIGHTNBO3_WRAPPER span.PSHYPERLINKDISABLED") var toDos: Int = 0,
    @Selector("div#SSSGROUPBOXRIGHTNBO4_WRAPPER > span") var cumGPA: Float = 0f,
    @Selector("div#SSSGROUPBOXRIGHTNBO5_WRAPPER > span") var lastSemGPA: Float = 0f,
    @Selector(
        "div#SSSGROUPBOXRIGHTNBO6_WRAPPER > span",
        converter = HoursEnrolledConverter::class
    ) var hoursEnrolled: Map<String, Int> = emptyMap(),
    @Selector(
        "div#SSSGROUPBOXRIGHTNBO7_WRAPPER > span",
        converter = DeclaredSubjectConverter::class
    ) var subject: DeclaredSubject = DeclaredSubject()
) {
    constructor() : this(0, 0, 0f, 0f, emptyMap(), DeclaredSubject())
}*/

/*data class GPA(
    @Selector("span.PSHYPERLINKDISABLED") var gpa: Float = 0f,
    @Selector("span.DASHBOARDDESCR") var type: String = ""
)*/

/*class SAMLResponseConverter : ElementConverter<String> {
    override fun convert(node: Element, selector: Selector): String = node.`val`()
}*/

/*data class SAMLResponse(
    @Selector(
        "form[method=\"post\"] > input[name=\"SAMLResponse\"]",
        converter = SAMLResponseConverter::class
    )
    var value: String = ""
) {
    constructor() : this("")
}*/

/*open class PostData(
    @Selector("div#win0divPSHIDDENFIELDS")
    val postData: MutableMap<String, String> = mutableMapOf()
) {
    constructor() : this(mutableMapOf())
}*/

/*class TermPostDataConverter : ElementConverter<Map<String, String>> {
    override fun convert(node: Element, selector: Selector): Map<String, String>? =
        node.select("input")?.associate { it.attr("id") to it.`val`() } ?: emptyMap()
}*/

/*@Selector("div#ptifrmcontent > div#ptifrmtarget iframe#ptifrmtgtframe html.chrome > body.PSPAGE")
data class GradeTerms(
    @Selector("table.PSLEVEL2GRID tr[id^=trSSR_DUMMY_RECV1$0_row]") var terms: MutableList<Term> = mutableListOf()
) : PostData() {
    constructor() : this(mutableListOf())
}*/

/*data class Term(
    @Selector("div[id^=win0divTERM_CAR] > span.PSEDITBOX_DISPONLY") var term: String = "",
    @Selector("div[id^=win0divCAREER] > span.PSEDITBOX_DISPONLY") var career: String = "",
    @Selector("div[id^=win0divINSTITUTION] > span.PSEDITBOX_DISPONLY") var institution: String = ""
) {
    constructor() : this("", "", "")
}*/

/*@Selector("form#SSR_SSENRL_GRADE > div.ps_pagecontainer > table.PSPAGECONTAINER > tbody")
data class ClassGrades(
    @Selector("tr div#win0divDERIVED_SSS_GRD_GROUPBOX2 table#ACE_DERIVED_SSS_GRD_GROUPBOX2 tr[id^=trTERM_CLASSES$0_row]") var grades: MutableList<Grade> = mutableListOf(),
    @Selector(
        "tr div#win0divDERIVED_SSS_GRD_GROUPBOX4 table#ACE_DERIVED_SSS_GRD_GROUPBOX4 tr[id=trTERM_STATS$0_row14] div[id=win0divSTATS_TERM$13] > span[id=STATS_TERM$13]",
        defValue = "0.0"
    ) var semesterGPA: Float = 0f,
    @Selector("tr div#win0divDERIVED_SSS_GRD_GROUPBOX4 table#ACE_DERIVED_SSS_GRD_GROUPBOX4 span#ACAD_STACTN_TBL_DESCRFORMAL") var academicStanding: String = ""
) {
    constructor() : this(mutableListOf(), 0f, "")
}*/

/*class FloatConverter : ElementConverter<Float> {
    override fun convert(node: Element, selector: Selector): Float =
        node.text().toFloatOrNull() ?: 0f
}*/

/*data class Grade(
    @Selector("div[id^=win0divCLS_LINK] a[id^=CLS_LINK]") var className: String = "",
    @Selector("div[id^=win0divCLASS_TBL_VW_DESCR]") var description: String = "",
    @Selector(
        "div[id^=win0divSTDNT_ENRL_SSV1_UNT_TAKEN]",
        converter = FloatConverter::class
    ) var units: Float = 0f,
    @Selector("div[id^=win0divGRADING_BASIS]") var grading: String = "",
    @Selector("div[id^=win0divSTDNT_ENRL_SSV1_CRSE_GRADE_OFF]") var grade: String = "",
    @Selector(
        "div[id^=win0divSTDNT_ENRL_SSV1_GRADE_POINTS]",
        converter = FloatConverter::class
    ) var gradePoints: Float = 0f
) {
    constructor() : this("", "", 0f, "", "", 0f)
}*/

//@Selector("form#SSR_SSENRL_GRADE > div.ps_pagecontainer > table.PSPAGECONTAINER > tbody div[id=win0divSTDNT_ENRL_SSV2$0] > table > tbody")
//data class ClassSchedule(
//    @Selector("div[id^=win0divDERIVED_REGFRM1_DESCR20] > table > tbody") var schedule: List<DetailedClass> = emptyList()
//) {
//    constructor() : this(emptyList())
//}

/*data class DetailedClass(
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
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_SCHED] > span") var datesAndTimes: MutableList<String> = mutableListOf(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_LOC] > span") var room: MutableList<String> = mutableListOf(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divDERIVED_CLS_DTL_SSR_INSTR_LONG] > span") var instructor: MutableList<String> = mutableListOf(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_DATES] > span") var startEndDate: MutableList<String> = mutableListOf(),
    @Selector("table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divMTG_AID$0] > span") var aidEligible: String = ""
) {
    constructor() : this("", "", "", 0f, "", "", 0, 0, "", mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), "")
}*/
data class Subject(val name: String = "", val gpa: Float = 0f)

data class StudentUndergradInfo(
    var holds: Int = 0,
    var toDos: Int = 0,
    var cumGPA: Float = 0f,
    var lastSemGPA: Float = 0f,
    var hoursEnrolled: Map<String, Int> = emptyMap(),
    var subject: DeclaredSubject = DeclaredSubject()
) {
    constructor() : this(0, 0, 0f, 0f, emptyMap(), DeclaredSubject())
}

data class GPA(
    var gpa: Float = 0f,
    var type: String = ""
)

data class DeclaredSubject(
    val majors: List<Subject> = emptyList(),
    val minors: List<Subject> = emptyList(),
    val gpaLastUpdated: Date = Calendar.getInstance().time
)

object UGInfo : ParseBuilder<StudentUndergradInfo>() {

    override val build: SimpleParser<StudentUndergradInfo> by buildParser(::StudentUndergradInfo) {
        int(
            "div#SSSGROUPBOXRIGHTNBO2_WRAPPER span.PSHYPERLINKDISABLED",
            StudentUndergradInfo::holds
        )
        int(
            "div#SSSGROUPBOXRIGHTNBO3_WRAPPER span.PSHYPERLINKDISABLED",
            StudentUndergradInfo::toDos
        )
        float(
            "div#SSSGROUPBOXRIGHTNBO4_WRAPPER span.PSHYPERLINKDISABLED",
            StudentUndergradInfo::cumGPA
        )
        float(
            "div#SSSGROUPBOXRIGHTNBO5_WRAPPER span.PSHYPERLINKDISABLED",
            StudentUndergradInfo::lastSemGPA
        )
        text("div#SSSGROUPBOXRIGHTNBO6_WRAPPER > span") { text, value: StudentUndergradInfo ->
            value.hoursEnrolled =
                with(text.trim()) {
                    substring(0, indexOf("Hours Enrolled")).trimEnd()
                }.split(Regex(": |\\s+")).filter { it.isNotBlank() }
                    .chunked(2) { (name, amount) -> name to amount.toInt() }
                    .toMap()
        }
        text("div#SSSGROUPBOXRIGHTNBO7_WRAPPER > span") { text, value: StudentUndergradInfo ->
            value.subject = with(text.trim()
                .split(Regex("\\)\\s"))
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

data class SAMLResponse(var value: String = "") {
    constructor() : this("")
}

object SAMLResponseAdapter : ParseBuilder<SAMLResponse>() {
    override val build by buildParser(::SAMLResponse) {
        element(
            "form[method=\"post\"] > input[name=\"SAMLResponse\"]",
            SAMLResponse::value,
            Element::`val`
        )
    }
}

data class CanvasProfileInfo(
    var display_name: String = "",
    var avatar_image_url: String = ""
)

data class CurrentUser(var current_user: CanvasProfileInfo = CanvasProfileInfo()) {
    constructor() : this(CanvasProfileInfo())
}

object CurrentUserAdapter : ParseBuilder<CurrentUser>() {

    override val build by buildParser(::CurrentUser) {
        element("div#application > script", CurrentUser::current_user) {
            try {
                moshi.adapter(CanvasProfileInfo::class.java).lenient().fromJson(
                    data().run {
                        val start = "\"current_user\":".let { indexOf(it) + it.length }
                        val end = indexOf("}", start) + 1
                        substring(
                            if (start in 0 until end) start else 0,
                            if (end in start until length) end else (length - 1)
                        ).also {
                            log("CurrentUserAdapterBuild", "$it - $start - $end - ${it.length}")
                        }
                    }
                ) ?: throw JsonDataException("Null Profile Info")
            } catch (e: JsonDataException) {
                e.printStackTrace(); CanvasProfileInfo("Unknown", "")
            }
        }
    }
}

inline fun <reified P : PostData> SimpleParser<P>.getPostData(property: KProperty1<P, MutableMap<String, String>>) =
    map("div#win0divPSHIDDENFIELDS input", property, { "id".let(it::attr) }, Element::`val`)
//    element("div#win0divPSHIDDENFIELDS") { element, value: PostData ->
//        value.postData = element.select("input")?.associate { it.attr("id") to it.`val`() } ?: emptyMap()
//    }

open class PostData(val postData: MutableMap<String, String> = mutableMapOf()) {
    constructor() : this(mutableMapOf())
}

object PostDataBuilder : ParseBuilder<PostData>() {
    override val build: SimpleParser<PostData> by buildParser(::PostData) {
        getPostData(PostData::postData)
    }
}

data class GradeTerms(var terms: MutableList<Term> = mutableListOf()) : PostData() {
    constructor() : this(mutableListOf())
}

data class Term(
    var term: String = "",
    var career: String = "",
    var institution: String = ""
) {
    constructor() : this("")
}

object TermBuilder : ParseBuilder<Term>() {
    override val build: SimpleParser<Term> by buildParser(::Term) {
        text("div[id^=win0divTERM_CAR] > span.PSEDITBOX_DISPONLY", Term::term)
        text("div[id^=win0divCAREER] > span.PSEDITBOX_DISPONLY", Term::career)
        text("div[id^=win0divINSTITUTION] > span.PSEDITBOX_DISPONLY", Term::institution)
    }
}

object GradeTermsBuilder : ParseBuilder<GradeTerms>() {
    override val build: SimpleParser<GradeTerms> by buildParser(::GradeTerms) {
        getPostData(GradeTerms::postData)
        collection(
            "table.PSLEVEL2GRID tr[id^=trSSR_DUMMY_RECV1$0_row]",
            GradeTerms::terms,
            TermBuilder()
        )
    }
}

data class ClassGrades(
    var grades: MutableList<Grade> = mutableListOf(),
    var semesterGPA: Float = 0f,
    var academicStanding: String = ""
) {
    constructor() : this(mutableListOf())
}

object ClassGradesBuilder : ParseBuilder<ClassGrades>() {
    override val build: SimpleParser<ClassGrades> by buildParser(::ClassGrades) {
        parser("form#SSR_SSENRL_GRADE > div.ps_pagecontainer > table.PSPAGECONTAINER > tbody") {
            collection(
                "tr div#win0divDERIVED_SSS_GRD_GROUPBOX2 table#ACE_DERIVED_SSS_GRD_GROUPBOX2 tr[id^=trTERM_CLASSES$0_row]",
                ClassGrades::grades,
                GradeBuilder()
            )
            float(
                "tr div#win0divDERIVED_SSS_GRD_GROUPBOX4 table#ACE_DERIVED_SSS_GRD_GROUPBOX4 tr[id=trTERM_STATS$0_row14] div[id=win0divSTATS_TERM$13] > span[id=STATS_TERM$13]",
                ClassGrades::semesterGPA
            )
            text(
                "tr div#win0divDERIVED_SSS_GRD_GROUPBOX4 table#ACE_DERIVED_SSS_GRD_GROUPBOX4 span#ACAD_STACTN_TBL_DESCRFORMAL",
                ClassGrades::academicStanding
            )
        }
    }

}

data class Grade(
    var className: String = "",
    var description: String = "",
    var units: Float = 0f,
    var grading: String = "",
    var grade: String = "",
    var gradePoints: Float = 0f
) {
    constructor() : this("")
}


object GradeBuilder : ParseBuilder<Grade>() {
    override val build: SimpleParser<Grade> by buildParser(::Grade) {
        text("div[id^=win0divCLS_LINK] a[id^=CLS_LINK]", Grade::className)
        text("div[id^=win0divCLASS_TBL_VW_DESCR]", Grade::description)
        float("div[id^=win0divSTDNT_ENRL_SSV1_UNT_TAKEN]", Grade::units)
        text("div[id^=win0divGRADING_BASIS]", Grade::grading)
        text("div[id^=win0divSTDNT_ENRL_SSV1_CRSE_GRADE_OFF]", Grade::grade)
        float("div[id^=win0divSTDNT_ENRL_SSV1_GRADE_POINTS]", Grade::gradePoints)
    }
}

inline class ClassSchedule(val schedule: MutableList<DetailedClass> = mutableListOf()) {
    constructor() : this(mutableListOf())
}

object ClassScheduleBuilder : ParseBuilder<ClassSchedule>() {
    override val build: SimpleParser<ClassSchedule> by buildParser(::ClassSchedule) {
        parser("form#SSR_SSENRL_GRADE > div.ps_pagecontainer > table.PSPAGECONTAINER > tbody") {
            collection(
                "div[id=win0divSTDNT_ENRL_SSV2$0] > table > tbody",
                ClassSchedule::schedule,
                DetailedClassBuilder()
            )
        }
    }
}

data class DetailedClass(
    var title: String = "",
    var status: String = "",
    var reason: String = "",
    var units: Float = 0f,
    var grading: String = "",
    var grade: String = "",
    var classNumber: Int = 0,
    var section: Int = 0,
    var component: String = "",
    var datesAndTimes: MutableList<String> = mutableListOf(),
    var room: MutableList<String> = mutableListOf(),
    var instructor: MutableList<String> = mutableListOf(),
    var startEndDate: MutableList<String> = mutableListOf(),
    var aidEligible: String = ""
) {
    constructor() : this("")
}

object DetailedClassBuilder : ParseBuilder<DetailedClass>() {
    override val build: SimpleParser<DetailedClass> by buildParser(::DetailedClass) {
        text("td.PAGROUPDIVIDER", DetailedClass::title)
        text(
            "table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=STATUS$0]",
            DetailedClass::status
        )
        text(
            "table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=ENRLSTATUSREASON$0]",
            DetailedClass::reason
        )
        float(
            "table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=DERIVED_REGFRM1_UNT_TAKEN$0]",
            DetailedClass::units
        )
        text(
            "table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=GB_DESCR$0]",
            DetailedClass::grading
        )
        text(
            "table[id^=SSR_DUMMY_RECVW\$scroll] tr[id^=trSSR_DUMMY_RECVW] span[id=DERIVED_REGFRM1_CRSE_GRADE_OFF$0]",
            DetailedClass::grade
        )
        int(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divDERIVED_CLS_DTL_CLASS_NBR$0] > span",
            DetailedClass::classNumber
        )
        int(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divDERIVED_CLS_DTL_CLASS_NBR$0] > span",
            DetailedClass::section
        )
        text(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divMTG_COMP$0] > span",
            DetailedClass::component
        )
        collection(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_SCHED] > span",
            DetailedClass::datesAndTimes,
            Element::text
        )
        collection(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_LOC] > span",
            DetailedClass::room,
            Element::text
        )
        collection(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divDERIVED_CLS_DTL_SSR_INSTR_LONG] > span",
            DetailedClass::instructor,
            Element::text
        )
        collection(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id^=trCLASS_MTG_VW] div[id^=win0divMTG_DATES] > span",
            DetailedClass::startEndDate,
            Element::text
        )
        text(
            "table[id^=CLASS_MTG_VW\$scroll] tr[id=trCLASS_MTG_VW$0_row1] div[id=win0divMTG_AID$0] > span",
            DetailedClass::aidEligible
        )
    }
}

fun <T : Any> buildParser(
    instanceGenerator: () -> T,
    builder: SimpleParser<T>.() -> Unit
): Lazy<SimpleParser<T>> =
    lazy { KSoup.build(instanceGenerator, builder) }