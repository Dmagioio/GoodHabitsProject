package com.example.goodhabits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodhabits.data.Habit
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

@Composable
fun WeeklyHabitCard(
    habit: Habit,
    onToggleForDate: (LocalDate) -> Unit,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val dates = (6 downTo 0).map { today.minusDays(it.toLong()) }

    val habitColor = Color(habit.colorHex.toInt())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleMedium
            )

            WeekDatesRow(
                dates = dates,
                habit = habit,
                onToggleForDate = onToggleForDate
            )
        }
    }
}

@Composable
fun WeekDatesRow(
    dates: List<LocalDate>,
    habit: Habit,
    onToggleForDate: (LocalDate) -> Unit
) {
    val habitColor = Color(habit.colorHex.toInt())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dates.forEach { date ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, date.year)
                set(Calendar.MONTH, date.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            }
            val dayFormatter = SimpleDateFormat("E", Locale("uk"))
            val dayName = dayFormatter.format(cal.time)
            val key = date.toEpochDay()
            val isSelected = habit.completedDates.contains(key)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                DateCircle(
                    number = date.dayOfMonth,
                    selected = isSelected,
                    color = habitColor
                ) {
                    onToggleForDate(date)
                }
            }
        }
    }
}

