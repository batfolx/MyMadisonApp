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

import androidx.room.Entity
import java.util.*

@Entity(tableName = "terms", primaryKeys = ["semester", "year"])
data class Term(
    val semester: String,
    val year: Int,
    val career: String,
    val institution: String,
    val courses: List<Course>,
    val semesterGPA: Float,
    val academicStanding: String
)

@Entity(tableName = "courses", primaryKeys = ["department", "number"])
data class Course(
    val department: String,
    val number: String,
    val description: String,
    val credits: Float,
    val grading: String,
    val grade: String,
    val gradePoints: Float,
    val status: String = "",
    val statusReason: String = "",
    val classNumber: Int = 0,
    val section: Int = 0,
    val component: String = "",
    val dateTimes: List<String> = emptyList(),
    val rooms: List<String> = emptyList(),
    val instructors: List<String> = emptyList(),
    val startEndDate: List<Pair<Date, Date>> = emptyList(),
    val aidEligible: String = ""
)