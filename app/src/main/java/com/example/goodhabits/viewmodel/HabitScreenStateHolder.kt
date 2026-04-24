package com.example.goodhabits.viewmodel

import com.example.goodhabits.domain.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import java.time.LocalDate

internal class HabitScreenStateHolder {
    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    fun updateHabits(habits: List<Habit>) {
        _uiState.update { state -> 
            state.copy(
                habits = habits, 
                isLoading = false,
                currentStreak = calculateStreak(habits)
            ) 
        }
    }

    private fun calculateStreak(habits: List<Habit>): Int {
        if (habits.isEmpty()) return 0
        
        val allCompletedDates = habits
            .flatMap { it.completedDates }
            .map { LocalDate.ofEpochDay(it) }
            .distinct()
            .sortedDescending()

        if (allCompletedDates.isEmpty()) return 0

        var streak = 0
        var currentDate = LocalDate.now()
        
        if (allCompletedDates.first() != currentDate) {
            currentDate = currentDate.minusDays(1)
            if (allCompletedDates.isEmpty() || allCompletedDates.first() != currentDate) {
                return 0
            }
        }

        for (date in allCompletedDates) {
            if (date == currentDate) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else if (date.isBefore(currentDate)) {
                break
            }
        }

        return streak
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error, isLoading = false) }
    }
}
