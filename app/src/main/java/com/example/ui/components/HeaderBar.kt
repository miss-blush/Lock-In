package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalSanctuaryTheme
import com.example.ui.theme.SanctuaryTheme

@Composable
fun HeaderBar(
    selectedTheme: SanctuaryTheme,
    onThemeSelect: (SanctuaryTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalSanctuaryTheme.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(theme.cardBg)
            .border(1.dp, theme.borderColor, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("header_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sanctuary Branding & Crest
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, theme.borderColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_fg),
                        contentDescription = "Lock In Crest",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = "Lock In",
                        color = theme.textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Deep Focus Sanctuary",
                        color = theme.subtextColor,
                        fontSize = 11.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }

            // Theme Dropdown Pill
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(1.dp, theme.borderColor, RoundedCornerShape(14.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("theme_selector_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.accentColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedTheme.displayName,
                        color = theme.textColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Theme",
                        tint = theme.subtextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color(0xFF14110F))
                        .border(1.dp, theme.borderColor, RoundedCornerShape(12.dp))
                ) {
                    SanctuaryTheme.entries.forEach { t ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(t.accentColor)
                                    )
                                    Text(
                                        text = t.displayName,
                                        color = if (t == selectedTheme) t.accentColor else Color(0xFFF5ECE5),
                                        fontSize = 12.sp,
                                        fontWeight = if (t == selectedTheme) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            onClick = {
                                onThemeSelect(t)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
