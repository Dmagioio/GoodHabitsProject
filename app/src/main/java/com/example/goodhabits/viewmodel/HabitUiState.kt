package com.example.goodhabits.viewmodel

import com.example.goodhabits.domain.analysis.TimeAdaptationSuggestion
import com.example.goodhabits.domain.model.Habit

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentStreak: Int = 0,
    val timeSuggestions: Map<Int, TimeAdaptationSuggestion> = emptyMap(),
    val dismissedSuggestions: Set<Int> = emptySet(),
    val sentStackingRemindersToday: Set<Int> = emptySet()
)
