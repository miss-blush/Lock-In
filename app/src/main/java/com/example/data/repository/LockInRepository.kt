package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.data.model.DailyPerformance
import com.example.data.model.HabitDay
import com.example.data.model.ScholarPreferences
import com.example.data.model.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class LockInRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val taskDao = db.taskDao()
    private val habitDao = db.habitDao()
    private val dailyPerfDao = db.dailyPerfDao()

    private val prefs: SharedPreferences = context.getSharedPreferences("lockin_prefs", Context.MODE_PRIVATE)

    private val _scholarPreferences = MutableStateFlow(loadPreferences())
    val scholarPreferences = _scholarPreferences.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    // TASKS
    fun getTasks(): Flow<List<TaskItem>> = taskDao.getAllTasks()

    suspend fun addTask(text: String): Long {
        val task = TaskItem(text = text.trim(), isDone = false)
        return taskDao.insertTask(task)
    }

    suspend fun toggleTask(task: TaskItem) {
        taskDao.updateTask(task.copy(isDone = !task.isDone))
    }

    suspend fun deleteTask(task: TaskItem) {
        taskDao.deleteTask(task)
    }

    // HABITS
    fun getHabits(monthKey: String): Flow<List<HabitDay>> = habitDao.getHabitsForMonth(monthKey)

    suspend fun toggleHabitDay(monthKey: String, dayIndex: Int, currentStatus: Boolean) {
        habitDao.upsertHabit(HabitDay(monthKey = monthKey, dayIndex = dayIndex, isCompleted = !currentStatus))
    }

    suspend fun resetMonthHabits(monthKey: String) {
        val freshHabits = (1..30).map { HabitDay(monthKey = monthKey, dayIndex = it, isCompleted = false) }
        habitDao.upsertAllHabits(freshHabits)
    }

    // DAILY PERFORMANCE
    fun getDailyPerformance(dateKey: String): Flow<DailyPerformance?> = dailyPerfDao.getPerfForDate(dateKey)

    suspend fun saveDailyPerformance(perf: DailyPerformance) {
        dailyPerfDao.upsertPerf(perf)
    }

    suspend fun logCompletedSessionMinutes(minutes: Int) {
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existing = dailyPerfDao.getPerfForDateSync(todayStr)
        val addHours = minutes / 60.0
        val updated = if (existing != null) {
            val rounded = ((existing.hours + addHours) * 10.0).let { Math.round(it) / 10.0 }
            existing.copy(hours = rounded)
        } else {
            DailyPerformance(
                dateKey = todayStr,
                hours = Math.round(addHours * 10.0) / 10.0,
                productivity = "100%",
                mood = "Locked In",
                thoughts = "Completed $minutes min focused study session."
            )
        }
        dailyPerfDao.upsertPerf(updated)
    }

    fun getPerformanceForDates(dates: List<String>): Flow<List<DailyPerformance>> {
        return dailyPerfDao.getPerfForDates(dates)
    }

    // PREFERENCES
    fun updatePreferences(newPrefs: ScholarPreferences) {
        prefs.edit()
            .putString("scholar_name", newPrefs.scholarName)
            .putString("academic_goal", newPrefs.academicGoal)
            .putFloat("daily_goal_hours", newPrefs.dailyGoalHours)
            .putBoolean("timer_chime", newPrefs.timerChimeAlert)
            .putString("theme_name", newPrefs.themeName)
            .putBoolean("wallpaper_enabled", newPrefs.wallpaperEnabled)
            .putString("brain_dump", newPrefs.brainDumpIntent)
            .apply()
        _scholarPreferences.value = newPrefs
    }

    fun saveBrainDump(dump: String) {
        val updated = _scholarPreferences.value.copy(brainDumpIntent = dump)
        updatePreferences(updated)
    }

    private fun loadPreferences(): ScholarPreferences {
        return ScholarPreferences(
            scholarName = prefs.getString("scholar_name", "Julian") ?: "Julian",
            academicGoal = prefs.getString("academic_goal", "NEET 2027 / Class 12 Boards") ?: "NEET 2027 / Class 12 Boards",
            dailyGoalHours = prefs.getFloat("daily_goal_hours", 6.0f),
            timerChimeAlert = prefs.getBoolean("timer_chime", true),
            themeName = prefs.getString("theme_name", "DARK_ACADEMIA") ?: "DARK_ACADEMIA",
            wallpaperEnabled = prefs.getBoolean("wallpaper_enabled", true),
            brainDumpIntent = prefs.getString("brainDumpIntent", "") ?: ""
        )
    }

    private suspend fun seedInitialDataIfEmpty() {
        val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val firstSeedKey = "has_seeded_initial_data"
        if (!prefs.getBoolean(firstSeedKey, false)) {
            // Seed habits (1..30)
            val habits = (1..30).map { day ->
                HabitDay(monthKey = currentMonth, dayIndex = day, isCompleted = day in listOf(1, 2, 3, 5, 6, 8, 9, 10))
            }
            habitDao.upsertAllHabits(habits)

            // Seed sample tasks
            val sampleTasks = listOf(
                TaskItem(text = "Review Organic Reaction Mechanisms (Aldehydes & Ketones)", isDone = true),
                TaskItem(text = "Complete 50 Practice Problems on Thermodynamics", isDone = true),
                TaskItem(text = "Active Recall flashcards for Human Physiology", isDone = false),
                TaskItem(text = "Mock Exam Error Log Analysis", isDone = false),
                TaskItem(text = "Formulate formula cheat sheet for Optics & Wave Motion", isDone = false)
            )
            sampleTasks.forEach { taskDao.insertTask(it) }

            // Seed weekly performance data
            val now = LocalDate.now()
            val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sampleWeekHours = mapOf(
                0 to 5.5, // Mon
                1 to 6.0, // Tue
                2 to 4.5, // Wed
                3 to 7.0, // Thu
                4 to 5.0, // Fri
                5 to 6.5, // Sat
                6 to 3.0  // Sun
            )
            for (i in 0..6) {
                val d = monday.plusDays(i.toLong())
                val dStr = d.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val hrs = sampleWeekHours[i] ?: 4.0
                dailyPerfDao.upsertPerf(
                    DailyPerformance(
                        dateKey = dStr,
                        hours = hrs,
                        productivity = if (hrs >= 6.0) "100%" else if (hrs >= 5.0) "75%" else "50%",
                        mood = if (hrs >= 6.0) "Locked In" else if (hrs >= 5.0) "Flow" else "Calm",
                        thoughts = "Deep revision completed. Consistent focus blocks and flashcard review."
                    )
                )
            }

            prefs.edit().putBoolean(firstSeedKey, true).apply()
        }
    }
}
