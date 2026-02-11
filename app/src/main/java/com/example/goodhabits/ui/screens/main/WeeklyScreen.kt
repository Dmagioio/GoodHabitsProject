package com.example.goodhabits.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.goodhabits.ui.components.WeeklyHabitCard
import java.time.LocalDate

@Composable
fun WeeklyScreen(
    habits: List<Habit>,
    onToggleHabitForDate: (Habit, LocalDate) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
    if (habits.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ще немає звичок для відстеження за тиждень",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(habits) { habit ->
                WeeklyHabitCard(
                    habit = habit,
                    onToggleForDate = { date ->
                        onToggleHabitForDate(habit, date)
                    },
                    onClick = { onHabitClick(habit) }
                )
            }
        }
    }
}

