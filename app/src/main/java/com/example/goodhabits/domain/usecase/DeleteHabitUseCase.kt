package com.example.goodhabits.domain.usecase

import com.example.goodhabits.domain.repository.HabitRepository
import com.example.goodhabits.domain.repository.ReminderScheduler
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(id: Int) {
        reminderScheduler.cancel(id)
        repository.deleteHabit(id)
    }
}
