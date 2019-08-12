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

import pl.droidsonroids.jspoon.annotation.Selector

@Selector("div#ptifrmcontent > div#ptifrmtarget iframe#ptifrmtgtframe html.chrome > body.PSPAGE")
data class ExpandAllPostData(
    @Selector(
        "div#win0divPSHIDDENFIELDS",
        converter = TermPostDataConverter::class
    )
    var expandPostData: Map<String, String> = emptyMap()
)

@Selector("div#win0divPAGECONTAINER div#win0divPSPAGECONTAINER > table#ACE_width tr > td > div[id=win0divSAA_ARSLT_RGVW$0] > table table[id=ACE_SAA_ARSLT_RGVW$0]")
data class Requirements(@Selector("tr div[id^=win0divDERIVED_SAA_DPR_GROUPBOX1] table") var requirements: List<RequirementSection> = emptyList())

data class RequirementSection(
    @Selector("tr PSGROUPBOXLABEL") var title: String = "",
    @Selector("tr table.PSGROUPBOXNBO table.PSGROUPBOX ") var description: String = "",
    @Selector("tr div[id^=win0divSAA_ARSLT_RLVW] > table tr table[id^=ACE_SAA_ARSLT_RLVW] > tbody") var subSections: List<SectionGroup> = emptyList()
)


data class SectionGroup(
    @Selector("div[id^=win0divDERIVED_SAA_DPR_GROUPBOX3GP]") var title: String = "",
    @Selector("div[id^=win0divDERIVED_SAA_DPR_SAA_DESCRLONG_05] > div > p > span") var description: String = "",
    @Selector("tr[id^=trSAA_ACRSE_VW]") var coursesUsedToSatisfy: List<SatisfiedCourse> = emptyList()
)

data class SatisfiedCourse(
    @Selector("div[id^=win0divCRSE_NAME]") var name: String = "",
    @Selector("div[id^=win0divCRSE_DESCR]") var description: String = "",
    @Selector("div[id^=win0divCRSE_UNITS]", defValue = "0") var units: Float = 0f,
    @Selector("div[id^=win0divCRSE_WHEN]") var whenTaken: String = "",
    @Selector("div[id^=win0divSAA_ACRSE_AVLVW_CRSE_GRADE_OFF]") var grade: String = "",
    @Selector("div[id^=win0divCRSE_STAT] > div > img", attr = "alt") var status: String = ""
)