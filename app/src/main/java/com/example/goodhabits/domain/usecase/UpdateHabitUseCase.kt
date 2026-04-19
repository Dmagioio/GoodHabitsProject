package com.example.goodhabits.domain.usecase

import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.repository.HabitRepository
import com.example.goodhabits.domain.repository.ReminderScheduler
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(habit: Habit) {
        val currentHabit = repository.getHabitById(habit.id) ?: return
        val updatedHabit = currentHabit.copy(
            title = habit.title,
            colorHex = habit.colorHex,
            days = habit.days,
            reminderTime = habit.reminderTime
        )

        repository.updateHabit(updatedHabit)

        if (updatedHabit.reminderTime != null) {
            reminderScheduler.schedule(updatedHabit.id, updatedHabit.title, updatedHabit.reminderTime)
        } else {
            reminderScheduler.cancel(updatedHabit.id)
        }
    }
}
