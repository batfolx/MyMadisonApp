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

package com.jmu.mymadisonapp.data

import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.isValid

class AcademicRequirementsRepository(private val client: MyMadisonService) {


    suspend fun getAcademicRequirements() {
        val postData = client.getAcademicRequirementsPostData().await()
        if (postData.isValid()) {

            log(
                "ExpandedAcademicRequirements",
                "Content: ${(client.expandAllAcademicRequirements(postData.body()!!.expandPostData.toMutableMap().apply {
                    this["ICAction"] = "DERIVED_SAA_DPR_SSS_EXPAND_ALL"
                    this["ICBcDomData"] = "UnknownValue"
                }.also { log("ExpandPostData", "Data: $it") }).await().body() ?: "Empty body")}"
            )
        }
    }

}