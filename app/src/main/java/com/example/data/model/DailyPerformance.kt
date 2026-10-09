package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_performance")
data class DailyPerformance(
    @PrimaryKey
    val dateKey: String, // YYYY-MM-DD
    val hours: Double = 0.0,
    val productivity: String = "100%", // "100%", "75%", "50%", "25%"
    val mood: String = "Locked In", // "Locked In", "Flow", "Calm", "Fatigued"
    val thoughts: String = "",
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 0
)
