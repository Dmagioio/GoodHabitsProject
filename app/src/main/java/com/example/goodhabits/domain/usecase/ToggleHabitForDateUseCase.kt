package com.example.goodhabits.domain.usecase

import com.example.goodhabits.domain.repository.HabitRepository
import javax.inject.Inject
import java.time.LocalDate

class ToggleHabitForDateUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Int, date: LocalDate) {
        repository.toggleHabitForDate(habitId, date)
    }
}
