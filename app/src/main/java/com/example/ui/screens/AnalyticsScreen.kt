package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyPerformance
import com.example.data.model.ScholarPreferences
import com.example.data.model.TaskItem
import com.example.ui.WeeklyDayStat
import com.example.ui.theme.LocalSanctuaryTheme
import com.example.ui.theme.PlayfairFontFamily
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val MOOD_OPTIONS = listOf(
    "Locked In" to "🎯 Locked In",
    "Flow" to "🌊 Flow",
    "Calm" to "☕ Calm",
    "Fatigued" to "🔋 Tired"
)

val PRODUCTIVITY_OPTIONS = listOf(
    "100%" to "100% 🔥",
    "75%" to "75% ⚡",
    "50%" to "50% 🟡",
    "25%" to "25% 🌧️"
)

@Composable
fun AnalyticsScreen(
    weeklyStats: List<WeeklyDayStat>,
    totalWeeklyHours: Double,
    selectedDate: LocalDate,
    dailyPerformance: DailyPerformance?,
    tasks: List<TaskItem>,
    preferences: ScholarPreferences,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onSelectToday: () -> Unit,
    onAdjustHours: (Double) -> Unit,
    onUpdateProductivity: (String) -> Unit,
    onUpdateMood: (String) -> Unit,
    onUpdateDailyThoughts: (String) -> Unit,
    onUpdateScholarProfile: (String, String, Float, Boolean, Boolean) -> Unit,
    onTestChime: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalSanctuaryTheme.current

    val currentPerf = dailyPerformance ?: DailyPerformance(
        dateKey = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        hours = 0.0,
        productivity = "100%",
        mood = "Locked In",
        thoughts = ""
    )

    val completedTasks = tasks.count { it.isDone }
    val totalTasks = tasks.size
    val formattedSelectedDate = selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy"))
    val isToday = selectedDate == LocalDate.now()

    var scholarNameInput by remember(preferences.scholarName) { mutableStateOf(preferences.scholarName) }
    var targetGoalInput by remember(preferences.academicGoal) { mutableStateOf(preferences.academicGoal) }
    var dailyGoalHoursInput by remember(preferences.dailyGoalHours) { mutableStateOf(preferences.dailyGoalHours.toString()) }
    var chimeAlertEnabled by remember(preferences.timerChimeAlert) { mutableStateOf(preferences.timerChimeAlert) }
    var wallpaperEnabled by remember(preferences.wallpaperEnabled) { mutableStateOf(preferences.wallpaperEnabled) }

    var prodDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. WEEKLY PRODUCTIVE HOURS BAR GRAPH CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(theme.cardBg)
                .border(1.dp, theme.borderColor, RoundedCornerShape(26.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Weekly Productive Hours",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = theme.textColor
                        )
                    }

                    Text(
                        text = "$totalWeeklyHours hrs total",
                        color = theme.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.testTag("weekly_total_label")
                    )
                }

                // BAR GRAPH CONTAINER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyStats.forEach { stat ->
                            val animatedHeight by animateFloatAsState(
                                targetValue = stat.percentageOfMax,
                                animationSpec = tween(durationMillis = 600),
                                label = "bar_height"
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Hours label above bar if > 0
                                if (stat.hours > 0) {
                                    Text(
                                        text = "${stat.hours}h",
                                        color = if (stat.isToday) theme.accentColor else theme.subtextColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }

                                // Pillar
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .fillMaxHeight(fraction = animatedHeight)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            if (stat.isToday) theme.accentColor
                                            else theme.accentColor.copy(alpha = 0.6f)
                                        )
                                        .border(
                                            1.dp,
                                            if (stat.isToday) theme.accentColor else theme.borderColor,
                                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                        )
                                        .testTag("bar_${stat.dayLabel.lowercase()}")
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Day Label (M, T, W, T, F, S, S)
                                Text(
                                    text = stat.dayLabel,
                                    color = if (stat.isToday) theme.textColor else theme.subtextColor,
                                    fontSize = 10.sp,
                                    fontWeight = if (stat.isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. DAILY PERFORMANCE TRACKER CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(theme.cardBg)
                .border(1.dp, theme.borderColor, RoundedCornerShape(26.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Date Nav
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Performance",
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = theme.textColor
                    )

                    // Date Switcher (< Today/Date >)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(
                            onClick = onPreviousDay,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("prev_day_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Day",
                                tint = theme.subtextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isToday) theme.accentSoftColor else Color.Black.copy(alpha = 0.3f))
                                .border(1.dp, theme.borderColor, RoundedCornerShape(10.dp))
                                .clickable { onSelectToday() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("date_indicator_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isToday) "Today" else formattedSelectedDate,
                                color = if (isToday) theme.textColor else theme.subtextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(
                            onClick = onNextDay,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("next_day_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Day",
                                tint = theme.subtextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 3 STATS CELLS (Study Hours, Productivity, Task Ratio)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Study Hours Stepper
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Study Hours",
                                color = theme.subtextColor,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "${currentPerf.hours}h",
                                color = theme.textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.testTag("perf_hours_display")
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.3f))
                                        .border(1.dp, theme.borderColor, RoundedCornerShape(6.dp))
                                        .clickable { onAdjustHours(-0.5) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("perf_hours_minus"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("-0.5", color = theme.subtextColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.3f))
                                        .border(1.dp, theme.borderColor, RoundedCornerShape(6.dp))
                                        .clickable { onAdjustHours(0.5) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("perf_hours_plus"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+0.5", color = theme.subtextColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Productivity Dropdown
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(16.dp))
                            .clickable { prodDropdownExpanded = true }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Productivity",
                                color = theme.subtextColor,
                                fontSize = 9.sp
                            )
                            val prodLabel = PRODUCTIVITY_OPTIONS.find { it.first == currentPerf.productivity }?.second
                                ?: "${currentPerf.productivity} 🔥"
                            Text(
                                text = prodLabel,
                                color = theme.accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.testTag("perf_prod_selector")
                            )
                            Text(
                                text = "Tap to change",
                                color = theme.subtextColor.copy(alpha = 0.6f),
                                fontSize = 8.sp
                            )
                        }

                        DropdownMenu(
                            expanded = prodDropdownExpanded,
                            onDismissRequest = { prodDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color(0xFF14110F))
                                .border(1.dp, theme.borderColor, RoundedCornerShape(12.dp))
                        ) {
                            PRODUCTIVITY_OPTIONS.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(text = label, color = Color.White, fontSize = 12.sp) },
                                    onClick = {
                                        onUpdateProductivity(value)
                                        prodDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Task Ratio
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(16.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Task Ratio",
                                color = theme.subtextColor,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "$completedTasks/$totalTasks",
                                color = theme.textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.testTag("perf_tasks_stat")
                            )
                            Text(
                                text = if (totalTasks > 0) "${((completedTasks.toFloat() / totalTasks) * 100).toInt()}% Done" else "0% Done",
                                color = theme.subtextColor,
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                // MINDSET & MOOD STATE
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Mindset & Mood State",
                        color = theme.subtextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MOOD_OPTIONS.forEach { (moodKey, moodLabel) ->
                            val isSelected = currentPerf.mood == moodKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) theme.accentSoftColor else Color.Black.copy(alpha = 0.2f))
                                    .border(
                                        1.dp,
                                        if (isSelected) theme.accentColor else theme.borderColor,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onUpdateMood(moodKey) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = moodLabel,
                                    color = if (isSelected) theme.textColor else theme.subtextColor,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // DAILY SUMMARY & REFLECTIONS JOURNAL
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Daily Summary & Reflections",
                        color = theme.subtextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = currentPerf.thoughts,
                        onValueChange = { onUpdateDailyThoughts(it) },
                        placeholder = {
                            Text(
                                text = "What did you achieve today? Write your daily reflections...",
                                color = theme.subtextColor.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("perf_thoughts_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = theme.borderColor,
                            focusedTextColor = theme.textColor,
                            unfocusedTextColor = theme.textColor,
                            cursorColor = theme.accentColor,
                            focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.15f)
                        ),
                        maxLines = 4
                    )
                }
            }
        }

        // 3. PERSONAL PREFERENCES & SETUP CARD ("Preferences Alag Se")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(theme.cardBg)
                .border(1.dp, theme.borderColor, RoundedCornerShape(26.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Personal Preferences & Setup",
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = theme.textColor
                    )
                }

                // Scholar Name
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Scholar Name",
                        color = theme.subtextColor,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = scholarNameInput,
                        onValueChange = {
                            scholarNameInput = it
                            onUpdateScholarProfile(
                                it,
                                targetGoalInput,
                                dailyGoalHoursInput.toFloatOrNull() ?: 6f,
                                chimeAlertEnabled,
                                wallpaperEnabled
                            )
                        },
                        placeholder = { Text("Your Name", fontSize = 12.sp, color = theme.subtextColor.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pref_name_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = theme.borderColor,
                            focusedTextColor = theme.textColor,
                            unfocusedTextColor = theme.textColor,
                            cursorColor = theme.accentColor,
                            focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.15f)
                        ),
                        singleLine = true
                    )
                }

                // Target Academic Goal
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Target Academic Goal",
                        color = theme.subtextColor,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = targetGoalInput,
                        onValueChange = {
                            targetGoalInput = it
                            onUpdateScholarProfile(
                                scholarNameInput,
                                it,
                                dailyGoalHoursInput.toFloatOrNull() ?: 6f,
                                chimeAlertEnabled,
                                wallpaperEnabled
                            )
                        },
                        placeholder = { Text("e.g. NEET 2027 / Class 12 Boards", fontSize = 12.sp, color = theme.subtextColor.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pref_goal_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = theme.borderColor,
                            focusedTextColor = theme.textColor,
                            unfocusedTextColor = theme.textColor,
                            cursorColor = theme.accentColor,
                            focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.15f)
                        ),
                        singleLine = true
                    )
                }

                // 2-Column: Daily Goal Hours & Timer Chime Alert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Daily Goal (Hours)",
                            color = theme.subtextColor,
                            fontSize = 11.sp
                        )
                        OutlinedTextField(
                            value = dailyGoalHoursInput,
                            onValueChange = {
                                dailyGoalHoursInput = it
                                onUpdateScholarProfile(
                                    scholarNameInput,
                                    targetGoalInput,
                                    it.toFloatOrNull() ?: 6f,
                                    chimeAlertEnabled,
                                    wallpaperEnabled
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pref_daily_hours_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = theme.accentColor,
                                unfocusedBorderColor = theme.borderColor,
                                focusedTextColor = theme.textColor,
                                unfocusedTextColor = theme.textColor,
                                cursorColor = theme.accentColor,
                                focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.15f)
                            ),
                            singleLine = true
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Timer Chime Alert",
                            color = theme.subtextColor,
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.2f))
                                .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (chimeAlertEnabled) "Enabled" else "Muted",
                                color = theme.textColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onTestChime,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Test Chime",
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Switch(
                                    checked = chimeAlertEnabled,
                                    onCheckedChange = {
                                        chimeAlertEnabled = it
                                        onUpdateScholarProfile(
                                            scholarNameInput,
                                            targetGoalInput,
                                            dailyGoalHoursInput.toFloatOrNull() ?: 6f,
                                            it,
                                            wallpaperEnabled
                                        )
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = theme.accentTextColor,
                                        checkedTrackColor = theme.accentColor,
                                        uncheckedThumbColor = theme.subtextColor,
                                        uncheckedTrackColor = Color.Black.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.testTag("pref_chime_switch")
                                )
                            }
                        }
                    }
                }

                // Aesthetic Wallpaper Overlay Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Aesthetic Wallpaper Art",
                            color = theme.textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Atmospheric library visuals in background",
                            color = theme.subtextColor,
                            fontSize = 10.sp
                        )
                    }

                    Switch(
                        checked = wallpaperEnabled,
                        onCheckedChange = {
                            wallpaperEnabled = it
                            onUpdateScholarProfile(
                                scholarNameInput,
                                targetGoalInput,
                                dailyGoalHoursInput.toFloatOrNull() ?: 6f,
                                chimeAlertEnabled,
                                it
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = theme.accentTextColor,
                            checkedTrackColor = theme.accentColor,
                            uncheckedThumbColor = theme.subtextColor,
                            uncheckedTrackColor = Color.Black.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("pref_wallpaper_switch")
                    )
                }
            }
        }
    }
}
