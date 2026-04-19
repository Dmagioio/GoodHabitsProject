package com.example.goodhabits.domain.usecase

import com.example.goodhabits.domain.repository.HabitRepository
import javax.inject.Inject

class ObserveHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke() = repository.observeHabits()
}
