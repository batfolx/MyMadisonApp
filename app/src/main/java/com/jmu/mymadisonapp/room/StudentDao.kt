package com.jmu.mymadisonapp.room

import androidx.lifecycle.LiveData
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertStudent(student: Student): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateStudent(student: Student)

    @Delete
    abstract fun deleteStudent(student: Student)

    @Query("SELECT * FROM student WHERE eID=:eID")
    abstract fun getStudent(eID: String): LiveData<Student>

}