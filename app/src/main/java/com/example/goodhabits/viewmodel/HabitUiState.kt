package com.example.goodhabits.viewmodel

import com.example.goodhabits.domain.model.Habit

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStreak: Int = 0
)
