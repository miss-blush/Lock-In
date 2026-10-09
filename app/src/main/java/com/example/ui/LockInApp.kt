package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.HeaderBar
import com.example.ui.components.NavigationTabBar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.FocusTimerScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.LocalSanctuaryTheme
import com.example.ui.theme.LockInTheme
import com.example.ui.theme.PlayfairFontFamily

@Composable
fun LockInApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val preferences by viewModel.preferences.collectAsState()

    val timerState by viewModel.timerState.collectAsState()
    val brainDumpText by viewModel.brainDumpText.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val habits by viewModel.habits.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailyPerformance by viewModel.dailyPerformance.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val totalWeeklyHours by viewModel.totalWeeklyHours.collectAsState()

    LockInTheme(sanctuaryTheme = currentTheme) {
        val theme = LocalSanctuaryTheme.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.backgroundFallback)
                .testTag("app_root")
        ) {
            // OPTIONAL ATMOSPHERIC WALLPAPER ARTWORK
            if (preferences.wallpaperEnabled && theme.drawableRes != null) {
                Image(
                    painter = painterResource(id = theme.drawableRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark glass scrim overlay for legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.72f))
                )
            }

            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 640.dp) // Responsive tablet centering
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. TOP HEADER & THEME SELECTOR
                        HeaderBar(
                            selectedTheme = currentTheme,
                            onThemeSelect = { viewModel.setTheme(it) }
                        )

                        // 2. SCHOLAR GREETING BADGE
                        if (preferences.scholarName.isNotBlank() || preferences.academicGoal.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(alpha = 0.35f))
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Scholar:",
                                        color = theme.subtextColor,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = preferences.scholarName,
                                        color = theme.textColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                if (preferences.academicGoal.isNotBlank()) {
                                    Text(
                                        text = preferences.academicGoal,
                                        color = theme.accentColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // 3. NAVIGATION TAB BAR (Focus, Habits, Tasks, Analytics)
                        NavigationTabBar(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // 4. MAIN ACTIVE SCREEN CONTENT
                        Crossfade(
                            targetState = currentTab,
                            label = "tab_crossfade"
                        ) { tab ->
                            when (tab) {
                                NavigationTab.FOCUS -> {
                                    FocusTimerScreen(
                                        timerState = timerState,
                                        brainDumpText = brainDumpText,
                                        onModeSelect = { viewModel.setTimerMode(it) },
                                        onToggleTimer = { viewModel.toggleTimer() },
                                        onResetTimer = { viewModel.resetTimer() },
                                        onAdjustTime = { viewModel.adjustTime(it) },
                                        onBrainDumpChange = { viewModel.onBrainDumpChange(it) },
                                        onLockInBrainDump = { viewModel.lockInBrainDump() },
                                        onDismissCelebration = { viewModel.dismissCelebrationDialog() }
                                    )
                                }

                                NavigationTab.HABITS -> {
                                    HabitsScreen(
                                        habits = habits,
                                        onToggleHabit = { day, status -> viewModel.toggleHabit(day, status) },
                                        onResetHabits = { viewModel.resetHabits() }
                                    )
                                }

                                NavigationTab.TASKS -> {
                                    TasksScreen(
                                        tasks = tasks,
                                        onAddTask = { viewModel.addTask(it) },
                                        onToggleTask = { viewModel.toggleTask(it) },
                                        onDeleteTask = { viewModel.deleteTask(it) }
                                    )
                                }

                                NavigationTab.ANALYTICS -> {
                                    AnalyticsScreen(
                                        weeklyStats = weeklyStats,
                                        totalWeeklyHours = totalWeeklyHours,
                                        selectedDate = selectedDate,
                                        dailyPerformance = dailyPerformance,
                                        tasks = tasks,
                                        preferences = preferences,
                                        onPreviousDay = { viewModel.selectPreviousDay() },
                                        onNextDay = { viewModel.selectNextDay() },
                                        onSelectToday = { viewModel.selectToday() },
                                        onAdjustHours = { viewModel.adjustSelectedDateHours(it) },
                                        onUpdateProductivity = { viewModel.updateProductivity(it) },
                                        onUpdateMood = { viewModel.updateMood(it) },
                                        onUpdateDailyThoughts = { viewModel.updateDailyThoughts(it) },
                                        onUpdateScholarProfile = { name, goal, hours, chime, wallpaper ->
                                            viewModel.updateScholarProfile(name, goal, hours, chime, wallpaper)
                                        },
                                        onTestChime = { viewModel.testChime() }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
