package com.example.goodhabits.data

data class Habit(
    val id: Int,
    val title: String,
    val colorHex: Long,
    val completedDates: Set<Long> = emptySet(),
    val days: Set<String> = emptySet(),
    val reminderTime: java.time.LocalTime? = null,
)

