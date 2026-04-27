package com.example.goodhabits.domain.model

import java.time.LocalTime

data class Habit(
    val id: Int = 0,
    val title: String,
    val colorHex: Long,
    val completedDates: Set<Long> = emptySet(),
    val days: Set<String> = emptySet(),
    val reminderTime: LocalTime? = null,
    val motivation: String = ""
)
