package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DailyPerformance
import com.example.data.model.HabitDay
import com.example.data.model.ScholarPreferences
import com.example.data.model.TaskItem
import com.example.data.repository.LockInRepository
import com.example.ui.theme.SanctuaryTheme
import com.example.util.SanctuaryAudio
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

enum class TimerMode(val title: String, val defaultMinutes: Int) {
    FOCUS("Focus", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}

enum class NavigationTab(val title: String) {
    FOCUS("Focus"),
    HABITS("Habits"),
    TASKS("Tasks"),
    ANALYTICS("Analytics")
}

data class TimerUiState(
    val mode: TimerMode = TimerMode.FOCUS,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val hasStarted: Boolean = false,
    val showCelebrationDialog: Boolean = false,
    val completedMinutes: Int = 25
) {
    val progress: Float
        get() = if (totalSeconds > 0) 1f - (remainingSeconds.toFloat() / totalSeconds.toFloat()) else 0f

    val formattedTime: String
        get() {
            val m = remainingSeconds / 60
            val s = remainingSeconds % 60
            return "%02d:%02d".format(m, s)
        }
}

data class WeeklyDayStat(
    val dayLabel: String,
    val dateKey: String,
    val hours: Double,
    val isToday: Boolean,
    val percentageOfMax: Float
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LockInRepository(application)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(NavigationTab.FOCUS)
    val currentTab = _currentTab.asStateFlow()

    // Scholar Preferences & Theme
    val preferences: StateFlow<ScholarPreferences> = repository.scholarPreferences

    val currentTheme: StateFlow<SanctuaryTheme> = preferences.combine(MutableStateFlow(Unit)) { prefs, _ ->
        try {
            SanctuaryTheme.valueOf(prefs.themeName)
        } catch (_: Exception) {
            SanctuaryTheme.DARK_ACADEMIA
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SanctuaryTheme.DARK_ACADEMIA)

    // TIMER STATE
    private val _timerState = MutableStateFlow(TimerUiState())
    val timerState = _timerState.asStateFlow()
    private var timerJob: Job? = null

    // TARGET BRAIN DUMP INTENT
    private val _brainDumpText = MutableStateFlow("")
    val brainDumpText = _brainDumpText.asStateFlow()

    // TASKS
    val tasks: StateFlow<List<TaskItem>> = repository.getTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // HABITS
    private val _currentMonthKey = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
    val currentMonthKey = _currentMonthKey.asStateFlow()

    val habits: StateFlow<List<HabitDay>> = _currentMonthKey.flatMapLatest { key ->
        repository.getHabits(key)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ANALYTICS & DAILY TRACKER
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val selectedDateKey = _selectedDate.combine(MutableStateFlow(Unit)) { date, _ ->
        date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    val dailyPerformance: StateFlow<DailyPerformance?> = selectedDateKey.flatMapLatest { key ->
        repository.getDailyPerformance(key)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Weekly stats
    private val currentWeekMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private val weekDateKeys = (0..6).map { currentWeekMonday.plusDays(it.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE) }

    val weeklyStats: StateFlow<List<WeeklyDayStat>> = repository.getPerformanceForDates(weekDateKeys)
        .combine(MutableStateFlow(Unit)) { perfList, _ ->
            val perfMap = perfList.associateBy { it.dateKey }
            val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
            val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val maxBench = 8.0 // 8 hours benchmark

            (0..6).map { i ->
                val date = currentWeekMonday.plusDays(i.toLong())
                val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val hours = perfMap[dateKey]?.hours ?: 0.0
                val pct = (hours / maxBench).coerceIn(0.08, 1.0).toFloat()
                WeeklyDayStat(
                    dayLabel = dayLabels[i],
                    dateKey = dateKey,
                    hours = hours,
                    isToday = dateKey == todayStr,
                    percentageOfMax = pct
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWeeklyHours: StateFlow<Double> = weeklyStats.combine(MutableStateFlow(Unit)) { list, _ ->
        val sum = list.sumOf { it.hours }
        Math.round(sum * 10.0) / 10.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        _brainDumpText.value = preferences.value.brainDumpIntent
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    // TIMER CONTROLS
    fun setTimerMode(mode: TimerMode) {
        timerJob?.cancel()
        val seconds = mode.defaultMinutes * 60
        _timerState.value = TimerUiState(
            mode = mode,
            totalSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = false,
            hasStarted = false
        )
    }

    fun toggleTimer() {
        val current = _timerState.value
        if (current.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = true, hasStarted = true)
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000L)
                val newSeconds = _timerState.value.remainingSeconds - 1
                if (newSeconds <= 0) {
                    onTimerFinished()
                    break
                } else {
                    _timerState.value = _timerState.value.copy(remainingSeconds = newSeconds)
                }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        val seconds = _timerState.value.mode.defaultMinutes * 60
        _timerState.value = TimerUiState(
            mode = _timerState.value.mode,
            totalSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = false,
            hasStarted = false
        )
    }

    fun adjustTime(secondsDelta: Int) {
        val current = _timerState.value
        val newRemaining = (current.remainingSeconds + secondsDelta).coerceAtLeast(60)
        val newTotal = (current.totalSeconds + secondsDelta).coerceAtLeast(60)
        _timerState.value = current.copy(
            remainingSeconds = newRemaining,
            totalSeconds = newTotal
        )
    }

    private fun onTimerFinished() {
        val finishedMode = _timerState.value.mode
        val completedMins = _timerState.value.totalSeconds / 60
        _timerState.value = _timerState.value.copy(
            remainingSeconds = 0,
            isRunning = false,
            showCelebrationDialog = true,
            completedMinutes = completedMins
        )

        // Play alert & vibrate
        SanctuaryAudio.playFocusChime(getApplication(), preferences.value.timerChimeAlert)

        // If focus session finished, log completed time
        if (finishedMode == TimerMode.FOCUS) {
            viewModelScope.launch {
                repository.logCompletedSessionMinutes(completedMins)
            }
        }
    }

    fun dismissCelebrationDialog() {
        _timerState.value = _timerState.value.copy(showCelebrationDialog = false)
        resetTimer()
    }

    fun onBrainDumpChange(text: String) {
        _brainDumpText.value = text
    }

    fun lockInBrainDump() {
        repository.saveBrainDump(_brainDumpText.value)
    }

    // TASKS ACTIONS
    fun addTask(taskText: String) {
        if (taskText.isBlank()) return
        viewModelScope.launch {
            repository.addTask(taskText.trim())
        }
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            repository.toggleTask(task)
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // HABITS ACTIONS
    fun toggleHabit(dayIndex: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitDay(_currentMonthKey.value, dayIndex, currentStatus)
        }
    }

    fun resetHabits() {
        viewModelScope.launch {
            repository.resetMonthHabits(_currentMonthKey.value)
        }
    }

    // DAILY PERFORMANCE ACTIONS
    fun selectPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun selectNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun selectToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun updateSelectedDateHours(newHours: Double) {
        val safeHours = (Math.round(newHours * 10.0) / 10.0).coerceAtLeast(0.0)
        saveCurrentDatePerformance { it.copy(hours = safeHours) }
    }

    fun adjustSelectedDateHours(delta: Double) {
        val currentHours = dailyPerformance.value?.hours ?: 0.0
        val newHours = (Math.round((currentHours + delta) * 10.0) / 10.0).coerceAtLeast(0.0)
        saveCurrentDatePerformance { it.copy(hours = newHours) }
    }

    fun updateProductivity(level: String) {
        saveCurrentDatePerformance { it.copy(productivity = level) }
    }

    fun updateMood(mood: String) {
        saveCurrentDatePerformance { it.copy(mood = mood) }
    }

    fun updateDailyThoughts(thoughts: String) {
        saveCurrentDatePerformance { it.copy(thoughts = thoughts) }
    }

    private fun saveCurrentDatePerformance(transform: (DailyPerformance) -> DailyPerformance) {
        val currentKey = _selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existing = dailyPerformance.value ?: DailyPerformance(
            dateKey = currentKey,
            hours = 0.0,
            productivity = "100%",
            mood = "Locked In",
            thoughts = ""
        )
        val updated = transform(existing)
        viewModelScope.launch {
            repository.saveDailyPerformance(updated)
        }
    }

    // PREFERENCES ACTIONS
    fun setTheme(theme: SanctuaryTheme) {
        val updated = preferences.value.copy(themeName = theme.name)
        repository.updatePreferences(updated)
    }

    fun updateScholarProfile(
        name: String,
        goal: String,
        dailyHours: Float,
        chimeEnabled: Boolean,
        wallpaperEnabled: Boolean
    ) {
        val updated = preferences.value.copy(
            scholarName = name,
            academicGoal = goal,
            dailyGoalHours = dailyHours,
            timerChimeAlert = chimeEnabled,
            wallpaperEnabled = wallpaperEnabled
        )
        repository.updatePreferences(updated)
    }

    fun testChime() {
        SanctuaryAudio.playFocusChime(getApplication(), true)
    }
}
