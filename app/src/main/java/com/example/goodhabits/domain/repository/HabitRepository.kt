package com.example.goodhabits.domain.repository

import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.model.HabitCompletionHistory
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

    fun observeAllHistory(): Flow<List<HabitCompletionHistory>>
    suspend fun getLastHistoryRecords(habitId: Int, limit: Int): List<HabitCompletionHistory>
}
