package com.jmu.mymadisonapp.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class Student(
    @PrimaryKey val eID: String,
    val displayName: String,
    val avatar: String
)