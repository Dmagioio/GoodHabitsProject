package com.example.goodhabits.data.mapper

import com.example.goodhabits.data.local.entity.HabitEntity
import com.example.goodhabits.domain.model.Habit
import javax.inject.Inject

class HabitMapper @Inject constructor() {
    fun toDomain(entity: HabitEntity): Habit = Habit(
        id = entity.id,
        title = entity.title,
        colorHex = entity.colorHex,
        completedDates = entity.completedDates,
        days = entity.days,
        reminderTime = entity.reminderTime
    )

    fun toEntity(domain: Habit): HabitEntity = HabitEntity(
        id = domain.id,
        title = domain.title,
        colorHex = domain.colorHex,
        completedDates = domain.completedDates,
        days = domain.days,
        reminderTime = domain.reminderTime
    )
}
