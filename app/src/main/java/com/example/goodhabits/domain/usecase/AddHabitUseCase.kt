package com.example.goodhabits.domain.usecase

import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.repository.HabitRepository
import com.example.goodhabits.domain.repository.ReminderScheduler
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(habit: Habit): Int {
        val habitId = repository.addHabit(habit)
        habit.reminderTime?.let { reminderScheduler.schedule(habitId, habit.title, it, habit.motivation) }
        return habitId
    }
}
