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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jmu.mymadisonapp.room.model.Converters
import com.jmu.mymadisonapp.room.model.Student
import com.jmu.mymadisonapp.room.model.Term

@Database(entities = [Student::class, Term::class], version = 1)
@TypeConverters(Converters::class)
abstract class MyMadisonDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun termDao(): TermDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE terms")
        database.execSQL("CREATE TABLE IF NOT EXISTS terms (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, semester TEXT NOT NULL, year INTEGER NOT NULL, career TEXT NOT NULL, institution TEXT NOT NULL, courses TEXT NOT NULL, semesterGPA REAL NOT NULL, academicStanding TEXT NOT NULL)")
    }
}