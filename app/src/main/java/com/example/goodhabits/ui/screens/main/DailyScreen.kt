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
import androidx.compose.ui.res.stringResource
import com.example.goodhabits.R
import com.example.goodhabits.domain.analysis.TimeAdaptationSuggestion
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.ui.components.DateHeader
import com.example.goodhabits.ui.components.HabitCard
import java.time.LocalDate

@Composable
fun DailyScreen(
    habits: List<Habit>,
    isLoading: Boolean = false,
    timeSuggestions: Map<Int, TimeAdaptationSuggestion> = emptyMap(),
    onToggleHabitToday: (Habit) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
    val today = LocalDate.now().toEpochDay()
    
    val todayNow = LocalDate.now()
    val internalDayOfWeek = when (todayNow.dayOfWeek) {
        java.time.DayOfWeek.SUNDAY -> "SU"
        java.time.DayOfWeek.MONDAY -> "MO"
        java.time.DayOfWeek.TUESDAY -> "TU"
        java.time.DayOfWeek.WEDNESDAY -> "WE"
        java.time.DayOfWeek.THURSDAY -> "TH"
        java.time.DayOfWeek.FRIDAY -> "FR"
        java.time.DayOfWeek.SATURDAY -> "SA"
    }

    val filteredHabits = habits.filter { habit ->
        habit.days.contains(internalDayOfWeek)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        DateHeader()
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(5) {
                    com.example.goodhabits.ui.components.HabitCardSkeleton()
                }
            }
        } else if (habits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_daily_habits_msg),
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
                        timeSuggestion = timeSuggestions[habit.id],
                        onToggleHabit = { onToggleHabitToday(habit) },
                        onClick = { onHabitClick(habit) }
                    )
                }
            }
        }
    }
}

