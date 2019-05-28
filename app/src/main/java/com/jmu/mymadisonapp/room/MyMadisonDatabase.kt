package com.jmu.mymadisonapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jmu.mymadisonapp.room.model.Student

@Database(entities = [Student::class], version = 1)
abstract class MyMadisonDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}