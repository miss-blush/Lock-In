package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.HabitDay
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE monthKey = :monthKey ORDER BY dayIndex ASC")
    fun getHabitsForMonth(monthKey: String): Flow<List<HabitDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHabit(habit: HabitDay)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllHabits(habits: List<HabitDay>)

    @Query("DELETE FROM habits WHERE monthKey = :monthKey")
    suspend fun clearMonthHabits(monthKey: String)
}
