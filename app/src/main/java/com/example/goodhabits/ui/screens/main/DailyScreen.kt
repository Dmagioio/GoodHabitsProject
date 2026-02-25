package com.example.goodhabits.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodhabits.data.Habit
import com.example.goodhabits.ui.components.DateHeader
import com.example.goodhabits.ui.components.HabitCard
import java.time.LocalDate

@Composable
fun DailyScreen(
    habits: List<Habit>,
    onToggleHabitToday: (Habit) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
    val today = LocalDate.now().toEpochDay()

    val todayNow = LocalDate.now()
    val dayOfWeek = todayNow.dayOfWeek.getDisplayName(
        java.time.format.TextStyle.SHORT,
        java.util.Locale("uk")
    ).replaceFirstChar { it.uppercase() }


    val filteredHabits = habits.filter { habit ->
        habit.days.contains(dayOfWeek)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        DateHeader()
        Spacer(modifier = Modifier.height(16.dp))

        if (habits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Натисніть +, щоб додати першу звичку",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredHabits) { habit ->
                    val isCompletedToday = habit.completedDates.contains(today)
                    HabitCard(
                        habit = habit,
                        isCompletedToday = isCompletedToday,
                        onToggleHabit = { onToggleHabitToday(habit) },
                        onClick = { onHabitClick(habit) }
                    )
                }
            }
        }
    }
}

