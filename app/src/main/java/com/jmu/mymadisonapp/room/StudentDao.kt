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

package com.jmu.mymadisonapp.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.room.*
import com.jmu.mymadisonapp.room.model.Student

@Dao
abstract class StudentDao {

    @Transaction
    open suspend fun upsert(student: Student): Long {
        val insertIndex = insertStudent(student)
        if (insertIndex == -1L)
            updateStudent(student)
        return insertIndex
    }

    open fun tryGetStudent(eID: String) = liveData {
        getStudent(eID).let {
            emitSource(
                if (it.value != null) (it as LiveData<Student?>)
                else getStudents().map { students -> students.firstOrNull { student -> student.eID.isNotBlank() } }
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertStudent(student: Student): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateStudent(student: Student)

    @Delete
    abstract fun deleteStudent(student: Student)

    @Query("SELECT * FROM students WHERE eID=:eID")
    abstract fun getStudent(eID: String): LiveData<Student>

    @Query("SELECT * FROM students")
    abstract fun getStudents(): LiveData<List<Student>>

}