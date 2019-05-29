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

package com.jmu.mymadisonapp.room.model

import android.icu.text.SimpleDateFormat
import androidx.room.TypeConverter
import com.jmu.mymadisonapp.moshi
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import java.util.*

class Converters {

    private inline fun <reified T> adapter() =
        moshi.adapter<List<T>>(Types.newParameterizedType(List::class.java, T::class.java))

    private inline fun <reified T> listToJson(adapter: JsonAdapter<List<T>>?, value: List<T>): String =
        adapter?.toJson(value) ?: ""

    private inline fun <reified T> jsonToList(adapter: JsonAdapter<List<T>>?, value: String): List<T> =
        adapter?.fromJson(value) ?: emptyList()

    @TypeConverter
    fun dateToString(date: Date) = SimpleDateFormat("MM/dd/yyyy").format(date)

    @TypeConverter
    fun stringToDate(dateString: String) = SimpleDateFormat("MM/dd/yyyy").parse(dateString)


    private val semesterUnitsAdapter = adapter<SemesterUnits>()
    @TypeConverter
    fun semesterUnitsToString(semesterUnits: List<SemesterUnits>) = semesterUnitsAdapter.toJson(semesterUnits)

    @TypeConverter
    fun stringToSemesterUnits(semesterUnits: String) = semesterUnitsAdapter.fromJson(semesterUnits)

    private val courseAdapter = adapter<Course>()
    @TypeConverter
    fun coursesToString(courses: List<Course>) = courseAdapter.toJson(courses)

    @TypeConverter
    fun stringToCourses(courses: String) = courseAdapter.fromJson(courses)

    private val degreeAdapter = adapter<DegreeGPA>()
    @TypeConverter
    fun degreeToString(degreeGPAs: List<DegreeGPA>) = degreeAdapter.toJson(degreeGPAs)

    @TypeConverter
    fun stringToDegree(degreeGPAs: String) = degreeAdapter.fromJson(degreeGPAs)

}