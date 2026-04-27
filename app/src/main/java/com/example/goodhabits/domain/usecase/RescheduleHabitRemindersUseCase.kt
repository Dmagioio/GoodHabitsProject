package com.example.goodhabits.domain.usecase

import com.example.goodhabits.domain.repository.HabitRepository
import com.example.goodhabits.domain.repository.ReminderScheduler
import javax.inject.Inject

class RescheduleHabitRemindersUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        val habits = repository.getHabits()
        habits.forEach { habit ->
            habit.reminderTime?.let { time ->
                reminderScheduler.schedule(habit.id, habit.title, time, habit.motivation)
            }
        }
    }
}
