package com.example.goodhabits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodhabits.data.Habit
import java.time.LocalDate

@Composable
fun OverallHabitCard(
    habit: Habit,
    onToggleToday: () -> Unit,
    onClick: () -> Unit
) {
    val todayKey = LocalDate.now().toEpochDay()
    val isCompletedToday = habit.completedDates.contains(todayKey)
    val habitColor = Color(habit.colorHex.toInt())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = habit.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                Checkbox(
                    checked = isCompletedToday,
                    onCheckedChange = { onToggleToday() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = habitColor,
                        checkmarkColor = Color.White,
                        uncheckedColor = habitColor
                    )
                )
            }

            DotGrid(
                completedDates = habit.completedDates,
                activeColor = habitColor
            )
        }
    }
}

@Composable
fun DotGrid(
    rows: Int = 6,
    columns: Int = 18,
    completedDates: Set<Long>,
    activeColor: Color
) {
    val totalDots = rows * columns
    val today = LocalDate.now()
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(rows) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(columns) { column ->
                    val index = row * columns + column
                    val daysAgo = (totalDots - 1) - index
                    val date = today.minusDays(daysAgo.toLong())
                    val key = date.toEpochDay()
                    val isActive = completedDates.contains(key)
                    Dot(
                        color = if (isActive) activeColor else Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

