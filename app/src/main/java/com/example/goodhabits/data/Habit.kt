package com.example.goodhabits.data

import androidx.compose.ui.graphics.Color

/**
 * Модель звички (Model layer).
 */
data class Habit(
    val id: Int,
    val title: String,
    val color: Color,
    val completedDates: Set<Long> = emptySet()
)

