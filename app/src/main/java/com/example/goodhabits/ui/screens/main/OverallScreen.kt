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
import com.example.goodhabits.ui.components.OverallHabitCard
@Composable
fun OverallScreen(
    habits: List<Habit>,
    onToggleHabitToday: (Habit) -> Unit,
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
                text = "Додайте звичку, щоб побачити загальну статистику",
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
                OverallHabitCard(
                    habit = habit,
                    onToggleToday = { onToggleHabitToday(habit) },
                    onClick = { onHabitClick(habit) }
                )
            }
        }
    }
}

