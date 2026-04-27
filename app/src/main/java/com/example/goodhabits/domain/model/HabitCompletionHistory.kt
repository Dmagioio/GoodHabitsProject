package com.example.goodhabits.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class HabitCompletionHistory(
    val id: Int = 0,
    val habitId: Int,
    val plannedTime: LocalTime?,
    val actualTime: LocalTime,
    val date: LocalDate
)
