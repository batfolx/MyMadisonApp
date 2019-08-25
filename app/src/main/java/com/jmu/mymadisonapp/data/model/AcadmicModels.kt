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

import com.timmahh.ksoup.ParseBuilder
import com.timmahh.ksoup.SimpleParser

/*@Selector("div#ptifrmcontent > div#ptifrmtarget iframe#ptifrmtgtframe html.chrome > body.PSPAGE")
data class ExpandAllPostData(
    @Selector(
        "div#win0divPSHIDDENFIELDS",
        converter = TermPostDataConverter::class
    )
    var expandPostData: Map<String, String> = emptyMap()
)*/

/*@Selector("div#win0divPAGECONTAINER div#win0divPSPAGECONTAINER > table#ACE_width tr > td > div[id=win0divSAA_ARSLT_RGVW$0] > table table[id=ACE_SAA_ARSLT_RGVW$0]")
data class Requirements(@Selector("tr div[id^=win0divDERIVED_SAA_DPR_GROUPBOX1] table") var requirements: MutableList<RequirementSection> = mutableListOf()) { constructor() : this(mutableListOf()) }*/

/*data class RequirementSection(
    @Selector("tr PSGROUPBOXLABEL") var title: String = "",
    @Selector("tr table.PSGROUPBOXNBO table.PSGROUPBOX ") var description: String = "",
    @Selector("tr div[id^=win0divSAA_ARSLT_RLVW] > table tr table[id^=ACE_SAA_ARSLT_RLVW] > tbody") var subSections: MutableList<SectionGroup> = mutableListOf()
) { constructor() : this("") }*/

/*data class SectionGroup(
    @Selector("div[id^=win0divDERIVED_SAA_DPR_GROUPBOX3GP]") var title: String = "",
    @Selector("div[id^=win0divDERIVED_SAA_DPR_SAA_DESCRLONG_05] > div > p > span") var description: String = "",
    @Selector("tr[id^=trSAA_ACRSE_VW]") var coursesUsedToSatisfy: MutableList<SatisfiedCourse> = mutableListOf()
) { constructor() : this("") }*/

/*data class SatisfiedCourse(
    @Selector("div[id^=win0divCRSE_NAME]") var name: String = "",
    @Selector("div[id^=win0divCRSE_DESCR]") var description: String = "",
    @Selector("div[id^=win0divCRSE_UNITS]", defValue = "0") var units: Float = 0f,
    @Selector("div[id^=win0divCRSE_WHEN]") var whenTaken: String = "",
    @Selector("div[id^=win0divSAA_ACRSE_AVLVW_CRSE_GRADE_OFF]") var grade: String = "",
    @Selector("div[id^=win0divCRSE_STAT] > div > img", attr = "alt") var status: String = ""
) { constructor() : this("") }*/

data class Requirements(var requirements: MutableList<RequirementSection> = mutableListOf()) {
    constructor() : this(mutableListOf())
}

object RequirementsBuilder : ParseBuilder<Requirements>() {
    override val build: SimpleParser<Requirements> by buildParser(::Requirements) {
        collection(
            "div#win0divPAGECONTAINER div#win0divPSPAGECONTAINER > table#ACE_width tr > td > div[id=win0divSAA_ARSLT_RGVW$0] > table table[id=ACE_SAA_ARSLT_RGVW$0] tr div[id^=win0divDERIVED_SAA_DPR_GROUPBOX1] table",
            Requirements::requirements,
            RequirementSectionBuilder()
        )
    }
}

data class RequirementSection(
    var title: String = "",
    var description: String = "",
    var subSections: MutableList<SectionGroup> = mutableListOf()
) {
    constructor() : this("")
}

object RequirementSectionBuilder : ParseBuilder<RequirementSection>() {
    override val build: SimpleParser<RequirementSection> by buildParser(::RequirementSection) {
        text("tr PSGROUPBOXLABEL", RequirementSection::title)
        text("tr table.PSGROUPBOXNBO table.PSGROUPBOX ", RequirementSection::description)
        collection(
            "tr div[id^=win0divSAA_ARSLT_RLVW] > table tr table[id^=ACE_SAA_ARSLT_RLVW] > tbody",
            RequirementSection::subSections,
            SectionGroupBuilder()
        )
    }
}


data class SectionGroup(
    var title: String = "",
    var description: String = "",
    var coursesUsedToSatisfy: MutableList<SatisfiedCourse> = mutableListOf()
) {
    constructor() : this("")
}

object SectionGroupBuilder : ParseBuilder<SectionGroup>() {
    override val build: SimpleParser<SectionGroup> by buildParser(::SectionGroup) {
        text("div[id^=win0divDERIVED_SAA_DPR_GROUPBOX3GP]", SectionGroup::title)
        text(
            "div[id^=win0divDERIVED_SAA_DPR_SAA_DESCRLONG_05] > div > p > span",
            SectionGroup::description
        )
        collection(
            "tr[id^=trSAA_ACRSE_VW]",
            SectionGroup::coursesUsedToSatisfy,
            SatisfiedCourseBuilder()
        )
    }
}

data class SatisfiedCourse(
    var name: String = "",
    var description: String = "",
    var units: Float = 0f,
    var whenTaken: String = "",
    var grade: String = "",
    var status: String = ""
) {
    constructor() : this("")
}

object SatisfiedCourseBuilder : ParseBuilder<SatisfiedCourse>() {
    override val build: SimpleParser<SatisfiedCourse> by buildParser(::SatisfiedCourse) {
        text("div[id^=win0divCRSE_NAME]", SatisfiedCourse::name)
        text("div[id^=win0divCRSE_DESCR]", SatisfiedCourse::description)
        float("div[id^=win0divCRSE_UNITS]", SatisfiedCourse::units)
        text("div[id^=win0divCRSE_WHEN]", SatisfiedCourse::whenTaken)
        text("div[id^=win0divSAA_ACRSE_AVLVW_CRSE_GRADE_OFF]", SatisfiedCourse::grade)
        element("div[id^=win0divCRSE_STAT] > div > img", SatisfiedCourse::status) { attr("alt") }
    }
}