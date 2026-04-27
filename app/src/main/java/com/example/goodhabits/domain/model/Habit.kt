package com.example.goodhabits.domain.model

import java.time.LocalDate
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

fun Habit.calculateStreak(): Int {
    if (completedDates.isEmpty()) return 0
    
    val today = LocalDate.now()
    val sortedDates = completedDates
        .map { LocalDate.ofEpochDay(it) }
        .filter { !it.isAfter(today) }
        .sortedDescending()

    if (sortedDates.isEmpty()) return 0

    var streak = 0
    var currentDate = today
    
    if (sortedDates.first() != today) {
        currentDate = today.minusDays(1)
        if (sortedDates.first() != currentDate) {
            return 0
        }
    }

    for (date in sortedDates) {
        if (date == currentDate) {
            streak++
            currentDate = currentDate.minusDays(1)
        } else if (date.isBefore(currentDate)) {
            break
        }
    }
    return streak
}
