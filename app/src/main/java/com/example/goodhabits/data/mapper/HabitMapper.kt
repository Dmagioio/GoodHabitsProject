package com.example.goodhabits.data.mapper

import com.example.goodhabits.data.local.entity.HabitCompletionHistoryEntity
import com.example.goodhabits.data.local.entity.HabitEntity
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.model.HabitCompletionHistory
import javax.inject.Inject

class HabitMapper @Inject constructor() {
    fun toDomain(entity: HabitEntity): Habit = Habit(
        id = entity.id,
        title = entity.title,
        colorHex = entity.colorHex,
        completedDates = entity.completedDates,
        days = entity.days,
        reminderTime = entity.reminderTime,
        motivation = entity.motivation
    )

    fun toEntity(domain: Habit): HabitEntity = HabitEntity(
        id = domain.id,
        title = domain.title,
        colorHex = domain.colorHex,
        completedDates = domain.completedDates,
        days = domain.days,
        reminderTime = domain.reminderTime,
        motivation = domain.motivation
    )

    fun historyToDomain(entity: HabitCompletionHistoryEntity): HabitCompletionHistory = HabitCompletionHistory(
        id = entity.id,
        habitId = entity.habitId,
        plannedTime = entity.plannedTime,
        actualTime = entity.actualTime,
        date = entity.date
    )

    fun historyToEntity(domain: HabitCompletionHistory): HabitCompletionHistoryEntity = HabitCompletionHistoryEntity(
        id = domain.id,
        habitId = domain.habitId,
        plannedTime = domain.plannedTime,
        actualTime = domain.actualTime,
        date = domain.date
    )
}
