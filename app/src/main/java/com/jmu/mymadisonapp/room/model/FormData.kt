package com.jmu.mymadisonapp.room.model

import androidx.room.Entity


@Entity(tableName = "FormData", primaryKeys = ["ICSID", "ICAction"])
data class FormData(
    var ICSID: String,
    var ICAction: String
)