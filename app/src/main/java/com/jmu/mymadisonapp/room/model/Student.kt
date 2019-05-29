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
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val eID: String,
    val displayName: String,
    val avatar: String,
    val holds: Int,
    val toDos: Int,
    val cumulativeGPA: Float,
    val lastSemesterGPA: Float,
    val hoursEnrolled: List<SemesterUnits>,
    val majors: List<DegreeGPA>,
    val minors: List<DegreeGPA>,
    val gpaLastUpdated: Date
)

data class SemesterUnits(val semester: String, val hours: Int)

data class DegreeGPA(val name: String, val gpa: Float)