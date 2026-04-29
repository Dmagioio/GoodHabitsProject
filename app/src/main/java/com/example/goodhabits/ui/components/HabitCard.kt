package com.example.goodhabits.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.goodhabits.R
import com.example.goodhabits.domain.analysis.TimeAdaptationSuggestion
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.model.calculateStreak
import com.example.goodhabits.ui.theme.StreakOrange
import java.time.format.DateTimeFormatter

@Composable
fun HabitCard(
    habit: Habit,
    isCompletedToday: Boolean,
    timeSuggestion: TimeAdaptationSuggestion? = null,
    onToggleHabit: () -> Unit,
    onClick: () -> Unit
) {
    val cardColor = Color(habit.colorHex.toInt())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val streak = habit.calculateStreak()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (streak > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔥 $streak",
                            color = StreakOrange,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    val plannedTimeStr = habit.reminderTime?.format(timeFormatter) ?: "--:--"
                    val suggestionText = timeSuggestion?.let { 
                        stringResource(R.string.usually_at, it.suggestedTime.format(timeFormatter))
                    } ?: ""
                    
                    Text(
                        text = "$plannedTimeStr$suggestionText",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                    )
                }
            }
            
            Checkbox(
                checked = isCompletedToday,
                onCheckedChange = { onToggleHabit() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    checkmarkColor = cardColor,
                    uncheckedColor = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}

