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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.theme.LocalSanctuaryTheme
import com.example.ui.theme.PlayfairFontFamily

@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    onAddTask: (String) -> Unit,
    onToggleTask: (TaskItem) -> Unit,
    onDeleteTask: (TaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalSanctuaryTheme.current
    val focusManager = LocalFocusManager.current
    var newTaskInput by remember { mutableStateOf("") }

    val completedCount = tasks.count { it.isDone }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tasks_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MAIN TASKS CONTAINER CARD
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
                // Header with completion count
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
                            imageVector = Icons.Default.Checklist,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Academic Tasks",
                                fontFamily = PlayfairFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = theme.textColor
                            )
                            Text(
                                text = "Curriculum & Syllabus Checklist",
                                color = theme.subtextColor,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.accentSoftColor)
                            .border(1.dp, theme.accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$completedCount/$totalCount Done",
                            color = theme.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Progress Bar
                if (totalCount > 0) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = theme.accentColor,
                        trackColor = Color.Black.copy(alpha = 0.3f)
                    )
                }

                // INPUT ROW FOR NEW TASK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newTaskInput,
                        onValueChange = { newTaskInput = it },
                        placeholder = {
                            Text(
                                text = "New target or topic to cover...",
                                color = theme.subtextColor.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_task_input"),
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (newTaskInput.isNotBlank()) {
                                onAddTask(newTaskInput)
                                newTaskInput = ""
                                focusManager.clearFocus()
                            }
                        })
                    )

                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(theme.accentColor)
                            .clickable {
                                if (newTaskInput.isNotBlank()) {
                                    onAddTask(newTaskInput)
                                    newTaskInput = ""
                                    focusManager.clearFocus()
                                }
                            }
                            .padding(horizontal = 16.dp)
                            .testTag("add_task_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = theme.accentTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Add",
                                color = theme.accentTextColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // TASK ITEMS LIST
                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active tasks. Add your syllabus milestones above.",
                            color = theme.subtextColor,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tasks.forEach { task ->
                            val itemBg by animateColorAsState(
                                targetValue = if (task.isDone) Color.Black.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.25f),
                                label = "task_bg"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(itemBg)
                                    .border(
                                        1.dp,
                                        if (task.isDone) theme.borderColor.copy(alpha = 0.08f) else theme.borderColor,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("task_item_${task.id}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Task text with strikethrough if done
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = task.text,
                                            color = if (task.isDone) theme.subtextColor.copy(alpha = 0.5f) else theme.textColor,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Action buttons (Done toggle & delete)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (task.isDone) theme.accentColor else Color.Black.copy(alpha = 0.2f))
                                                .border(
                                                    1.dp,
                                                    if (task.isDone) theme.accentColor else theme.borderColor,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable { onToggleTask(task) }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .testTag("toggle_task_${task.id}"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (task.isDone) "✓ Done" else "Pending",
                                                color = if (task.isDone) theme.accentTextColor else theme.subtextColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeleteTask(task) },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .testTag("delete_task_${task.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Task",
                                                tint = theme.subtextColor.copy(alpha = 0.6f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
