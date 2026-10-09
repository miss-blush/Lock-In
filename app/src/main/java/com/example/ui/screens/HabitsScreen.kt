package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitDay
import com.example.ui.theme.LocalSanctuaryTheme
import com.example.ui.theme.PlayfairFontFamily

@Composable
fun HabitsScreen(
    habits: List<HabitDay>,
    onToggleHabit: (Int, Boolean) -> Unit,
    onResetHabits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalSanctuaryTheme.current
    var showResetDialog by remember { mutableStateOf(false) }

    // Map habits by dayIndex (1..30)
    val habitMap = habits.associateBy { it.dayIndex }
    val completedCount = habits.count { it.isCompleted }
    val completionPct = ((completedCount / 30f) * 100).toInt()

    // Streak calculation (longest consecutive block from day 1 or recent streak)
    var currentStreak = 0
    for (i in 1..30) {
        if (habitMap[i]?.isCompleted == true) {
            currentStreak++
        } else {
            if (currentStreak > 0) break
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habits_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MAIN HABIT CARD
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "30-Day Habit Grid",
                                fontFamily = PlayfairFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = theme.textColor
                            )
                            Text(
                                text = "Consistency Sanctuary Tracker",
                                color = theme.subtextColor,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(12.dp))
                            .clickable { showResetDialog = true }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("reset_habits_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = theme.subtextColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Reset",
                                color = theme.subtextColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // STATS OVERVIEW CHIPS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Completed Days
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$completedCount/30",
                                color = theme.accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Days Completed",
                                color = theme.subtextColor,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Completion Rate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$completionPct%",
                                color = theme.accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Discipline Rate",
                                color = theme.subtextColor,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Current Streak
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${currentStreak}d 🔥",
                                color = theme.accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Current Streak",
                                color = theme.subtextColor,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // 30-DAY NUMBERED TILES GRID (6 columns x 5 rows)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(30) { index ->
                        val dayNumber = index + 1
                        val isDone = habitMap[dayNumber]?.isCompleted == true

                        val tileBg by animateColorAsState(
                            targetValue = if (isDone) theme.accentColor else Color.Black.copy(alpha = 0.22f),
                            label = "habit_tile_bg"
                        )
                        val tileText by animateColorAsState(
                            targetValue = if (isDone) theme.accentTextColor else theme.subtextColor,
                            label = "habit_tile_text"
                        )

                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(tileBg)
                                .border(
                                    1.dp,
                                    if (isDone) theme.accentColor else theme.borderColor,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onToggleHabit(dayNumber, isDone) }
                                .testTag("habit_day_$dayNumber"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$dayNumber",
                                color = tileText,
                                fontSize = 13.sp,
                                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Text(
                    text = "Tap a day tile to mark study session finished. Build your unbroken sacred chain.",
                    color = theme.subtextColor,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color(0xFF14100D),
            title = {
                Text(
                    text = "Reset 30-Day Grid?",
                    fontFamily = PlayfairFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = theme.textColor,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "This will clear all 30 days of habit checkmarks for this cycle. Are you sure you want to begin a fresh streak?",
                    color = theme.subtextColor,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetHabits()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.accentColor,
                        contentColor = theme.accentTextColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset Grid", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = theme.subtextColor, fontSize = 12.sp)
                }
            }
        )
    }
}
