package com.example.goodhabits.domain.repository

import com.example.goodhabits.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {
    fun observeHabits(): Flow<List<Habit>>

    suspend fun getHabits(): List<Habit>

    suspend fun getHabitById(id: Int): Habit?

    suspend fun addHabit(habit: Habit): Int

    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(id: Int)

    suspend fun toggleHabitForDate(id: Int, date: LocalDate)

    suspend fun deleteAllHabits()
}
