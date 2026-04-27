package com.example.goodhabits.viewmodel

import com.example.goodhabits.domain.analysis.BehavioralAnalysisEngine
import com.example.goodhabits.domain.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

internal class HabitScreenStateHolder(
    private val analysisEngine: BehavioralAnalysisEngine
) {
    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    suspend fun updateHabits(habits: List<Habit>) {
        val suggestions = habits.associate { habit ->
            habit.id to analysisEngine.analyzeTimeAdaptation(habit.id)
        }.filterValues { it != null }.mapValues { it.value!! }

        _uiState.update { state -> 
            state.copy(
                habits = habits, 
                isLoading = false,
                currentStreak = calculateStreak(habits),
                timeSuggestions = suggestions
            ) 
        }
    }

    private fun calculateStreak(habits: List<Habit>): Int {
        if (habits.isEmpty()) return 0
        
        val today = LocalDate.now()
        val allCompletedDates = habits
            .flatMap { it.completedDates }
            .map { LocalDate.ofEpochDay(it) }
            .filter { !it.isAfter(today) }
            .distinct()
            .sortedDescending()

        if (allCompletedDates.isEmpty()) return 0

        var streak = 0
        var currentDate = today
        
        // If nothing completed today, start checking from yesterday
        if (allCompletedDates.first() != currentDate) {
            currentDate = currentDate.minusDays(1)
            // If also nothing completed yesterday, streak is 0
            if (allCompletedDates.first() != currentDate) {
                return 0
            }
        }

        for (date in allCompletedDates) {
            if (date == currentDate) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else if (date.isBefore(currentDate)) {
                // Gap found
                break
            }
        }

        return streak
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun dismissSuggestion(habitId: Int) {
        _uiState.update { state ->
            state.copy(dismissedSuggestions = state.dismissedSuggestions + habitId)
        }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error, isLoading = false) }
    }

    fun markStackingReminderSent(habitId: Int) {
        _uiState.update { state ->
            state.copy(sentStackingRemindersToday = state.sentStackingRemindersToday + habitId)
        }
    }
}
