package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TimerMode
import com.example.ui.TimerUiState
import com.example.ui.theme.LocalSanctuaryTheme
import com.example.ui.theme.PlayfairFontFamily

val SANCTUARY_QUOTES = listOf(
    "\"Eliminate distractions. Lock in.\"",
    "\"Deep work is not an option; it is the craft.\"",
    "\"In stillness, mastery is forged.\"",
    "\"The library belongs to those who stay.\"",
    "\"Feed your focus, starve your distractions.\"",
    "\"Silence the noise; summon the scholar within.\""
)

@Composable
fun FocusTimerScreen(
    timerState: TimerUiState,
    brainDumpText: String,
    onModeSelect: (TimerMode) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAdjustTime: (Int) -> Unit,
    onBrainDumpChange: (String) -> Unit,
    onLockInBrainDump: () -> Unit,
    onDismissCelebration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalSanctuaryTheme.current
    val focusManager = LocalFocusManager.current

    var quoteIndex by remember { mutableIntStateOf(0) }
    var targetSavedMessage by remember { mutableStateOf(false) }

    LaunchedEffect(timerState.hasStarted) {
        if (timerState.hasStarted) {
            quoteIndex = (quoteIndex + 1) % SANCTUARY_QUOTES.size
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("focus_timer_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MAIN TIMER CARD
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TIMER MODE PILLS
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimerMode.entries.forEach { mode ->
                        val isSelected = mode == timerState.mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) theme.accentSoftColor else Color.Black.copy(alpha = 0.25f))
                                .border(
                                    1.dp,
                                    if (isSelected) theme.accentColor else theme.borderColor,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onModeSelect(mode) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("timer_mode_${mode.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.title,
                                color = if (isSelected) theme.textColor else theme.subtextColor,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // CIRCULAR TIMER DIAL & TIME DISPLAY
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = timerState.progress,
                        animationSpec = tween(durationMillis = 500),
                        label = "timer_progress"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 8.dp.toPx()
                        // Track background circle
                        drawCircle(
                            color = theme.borderColor,
                            radius = (size.minDimension - strokeWidth) / 2,
                            style = Stroke(width = strokeWidth)
                        )
                        // Progress Arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    theme.accentColor,
                                    theme.accentColor.copy(alpha = 0.7f),
                                    theme.accentColor
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = timerState.formattedTime,
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 52.sp,
                            color = theme.textColor,
                            letterSpacing = (-1).sp,
                            modifier = Modifier.testTag("timer_display_text")
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (timerState.isRunning) theme.accentColor else Color.Gray)
                            )
                            Text(
                                text = if (timerState.isRunning) "In Deep Session" else "Ready",
                                color = theme.subtextColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // INSPIRING QUOTE
                Text(
                    text = SANCTUARY_QUOTES[quoteIndex],
                    color = theme.subtextColor,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // ACTION BUTTON CONTROLS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // -5 min
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                            .clickable { onAdjustTime(-300) }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                            .testTag("timer_minus_5m"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "-5m",
                            color = theme.subtextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Main Begin / Pause / Resume Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(theme.accentColor)
                            .clickable { onToggleTimer() }
                            .padding(horizontal = 24.dp, vertical = 11.dp)
                            .testTag("timer_toggle_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (timerState.isRunning) "Pause" else "Start",
                                tint = theme.accentTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = when {
                                    timerState.isRunning -> "Pause"
                                    timerState.hasStarted -> "Resume"
                                    else -> "Begin Session"
                                },
                                color = theme.accentTextColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // +5 min
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                            .clickable { onAdjustTime(300) }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                            .testTag("timer_plus_5m"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+5m",
                            color = theme.subtextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Reset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.2f))
                            .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                            .clickable { onResetTimer() }
                            .padding(horizontal = 10.dp, vertical = 9.dp)
                            .testTag("timer_reset_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Timer",
                            tint = theme.subtextColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // SESSION TARGET INTENT (BRAIN DUMP CARD)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(theme.cardBg)
                .border(1.dp, theme.borderColor, RoundedCornerShape(24.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Session Target Intent",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = theme.textColor
                        )
                    }

                    Text(
                        text = "Mindful Focus",
                        color = theme.subtextColor,
                        fontSize = 11.sp
                    )
                }

                OutlinedTextField(
                    value = brainDumpText,
                    onValueChange = {
                        targetSavedMessage = false
                        onBrainDumpChange(it)
                    },
                    placeholder = {
                        Text(
                            text = "Write down your exact goal for this study session...",
                            color = theme.subtextColor.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("session_brain_dump_input"),
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
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onLockInBrainDump()
                        targetSavedMessage = true
                    })
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(theme.accentSoftColor)
                        .border(1.dp, theme.accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable {
                            focusManager.clearFocus()
                            onLockInBrainDump()
                            targetSavedMessage = true
                        }
                        .padding(vertical = 10.dp)
                        .testTag("lock_in_target_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (targetSavedMessage) "✓ Intent Locked In" else "Lock In Target",
                        color = theme.textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // SESSION COMPLETED CELEBRATION DIALOG
    if (timerState.showCelebrationDialog) {
        AlertDialog(
            onDismissRequest = onDismissCelebration,
            containerColor = Color(0xFF14100D),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = theme.accentColor
                    )
                    Text(
                        text = "Session Complete!",
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = theme.textColor,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Outstanding dedication! You successfully completed ${timerState.completedMinutes} minutes of focused immersion.",
                        color = theme.textColor,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "This time has been automatically recorded in your Daily Performance tracker.",
                        color = theme.subtextColor,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissCelebration,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.accentColor,
                        contentColor = theme.accentTextColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Deep Breath & Continue", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        )
    }
}
