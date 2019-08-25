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

import com.jmu.mymadisonapp.data.model.CurrentUser
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.net.checkLoggedIn
import com.jmu.mymadisonapp.net.isValid
import com.jmu.mymadisonapp.net.singleResource
import com.jmu.mymadisonapp.room.StudentDao
import com.jmu.mymadisonapp.room.model.DegreeGPA
import com.jmu.mymadisonapp.room.model.SemesterUnits
import com.jmu.mymadisonapp.room.model.Student
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.ResponseBody.Companion.toResponseBody
import org.jsoup.Jsoup
import retrofit2.Response
import java.util.*

class StudentRepository(private val client: MyMadisonService, private val studentDao: StudentDao) {

    fun isLoggedIn() = checkLoggedIn()

    suspend fun getStudentData(eID: String = "") =
        singleResource<Student> {
            loadFromDb { studentDao.tryGetStudent(eID) }
            createCall { getDashAndProfileAsync(eID) }
            saveResult { studentDao.upsert(it) }
            shouldFetch { it?.eID?.isEmpty() ?: true }
        }

    private fun getDashAndProfileAsync(eID: String) = GlobalScope.async {
        val response = parseEID()
        val profile = getCanvasProfileInfo()
        val ug = getUndergraduateDashboard()
        if (profile.isValid() && ug.isValid())
            Response.success(200,
                Student(
                    response.takeUnless { it.isEmpty() } ?: eID,
                    profile.body()!!.current_user.display_name,
                    profile.body()!!.current_user.avatar_image_url,
                    ug.body()!!.holds,
                    ug.body()!!.toDos,
                    ug.body()!!.cumGPA,
                    ug.body()!!.lastSemGPA,
                    ug.body()!!.hoursEnrolled.map { SemesterUnits(it.key, it.value) },
                    ug.body()!!.subject.majors.map { DegreeGPA(it.name, it.gpa) },
                    ug.body()!!.subject.minors.map { DegreeGPA(it.name, it.gpa) },
                    ug.body()!!.subject.gpaLastUpdated
                )
            )
        else Response.error(
            (profile.code() + ug.code()),
            profile.errorBody() ?: ug.errorBody() ?: "No data".toResponseBody(null)
        )
    }

    private suspend fun parseEID() = Jsoup.parse(
        client.checkLoggedIn().await()
            .body()?.string()
    )
        .select("header#top > table.eppbr_header_bg.EPPBRLAYOUTTABLE > tbody > tr:nth-child(2) > td:nth-child(7) > table:nth-child(1) > tbody > tr > td:nth-child(1) > nobr")
        .text().toLowerCase(Locale.getDefault())
        .also { log("eIDResponse", "eID: $it") }


    suspend fun getUndergraduateDashboard() = client.getUndergraduateDashboard().await()
        .also { log("UndergraduateDashboard", "Content: ${it.body() ?: "Empty body: $it"}}") }

    suspend fun getCanvasProfileInfo(): Response<CurrentUser> =
        (client.getSAMLResponse().await().body()?.value?.let {
            client.getCanvasProfileInfo(it).await()
        } ?: Response.success(299, CurrentUser()))
            .also { log("CanvasProfileInfo", "Current User: ${it.body() ?: "Empty body: $it"}") }


}