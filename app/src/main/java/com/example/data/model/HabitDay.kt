package com.example.data.model

import androidx.room.Entity

@Entity(tableName = "habits", primaryKeys = ["monthKey", "dayIndex"])
data class HabitDay(
    val monthKey: String,
    val dayIndex: Int, // 1 to 30
    val isCompleted: Boolean = false
)
