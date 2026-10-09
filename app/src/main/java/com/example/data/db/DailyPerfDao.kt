package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyPerformance
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPerfDao {
    @Query("SELECT * FROM daily_performance WHERE dateKey = :dateKey")
    fun getPerfForDate(dateKey: String): Flow<DailyPerformance?>

    @Query("SELECT * FROM daily_performance WHERE dateKey = :dateKey")
    suspend fun getPerfForDateSync(dateKey: String): DailyPerformance?

    @Query("SELECT * FROM daily_performance WHERE dateKey IN (:dates)")
    fun getPerfForDates(dates: List<String>): Flow<List<DailyPerformance>>

    @Query("SELECT * FROM daily_performance ORDER BY dateKey DESC")
    fun getAllHistory(): Flow<List<DailyPerformance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPerf(perf: DailyPerformance)
}
