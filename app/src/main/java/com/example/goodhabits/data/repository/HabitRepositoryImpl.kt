package com.example.goodhabits.data.repository

import com.example.goodhabits.data.local.datasource.HabitLocalDataSource
import com.example.goodhabits.data.local.entity.HabitCompletionHistoryEntity
import com.example.goodhabits.data.mapper.HabitMapper
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.model.HabitCompletionHistory
import com.example.goodhabits.domain.repository.HabitRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime

class HabitRepositoryImpl @Inject constructor(
    private val localDataSource: HabitLocalDataSource,
    private val mapper: HabitMapper
) : HabitRepository {
    override fun observeHabits(): Flow<List<Habit>> =
        localDataSource.observeHabits().map { entities -> entities.map(mapper::toDomain) }

    override suspend fun getHabits(): List<Habit> =
        localDataSource.getHabits().map(mapper::toDomain)

    override suspend fun getHabitById(id: Int): Habit? =
        localDataSource.getHabitById(id)?.let(mapper::toDomain)

    override suspend fun addHabit(habit: Habit): Int =
        localDataSource.insertHabit(mapper.toEntity(habit))

    override suspend fun updateHabit(habit: Habit) {
        localDataSource.updateHabit(mapper.toEntity(habit))
    }

    override suspend fun deleteHabit(id: Int) {
        localDataSource.deleteHabitById(id)
    }

    override suspend fun toggleHabitForDate(id: Int, date: LocalDate) {
        val habitEntity = localDataSource.getHabitById(id) ?: return
        val updatedDates = habitEntity.completedDates.toMutableSet()
        val key = date.toEpochDay()

        val isCompleting = updatedDates.add(key)
        if (!isCompleting) {
            updatedDates.remove(key)
        } else {
            localDataSource.insertHistory(
                HabitCompletionHistoryEntity(
                    habitId = id,
                    plannedTime = habitEntity.reminderTime,
                    actualTime = LocalTime.now(),
                    date = date
                )
            )
        }

        localDataSource.updateHabit(habitEntity.copy(completedDates = updatedDates))
    }

    override suspend fun deleteAllHabits() {
        localDataSource.deleteAll()
    }

    override fun observeAllHistory(): Flow<List<HabitCompletionHistory>> =
        localDataSource.getAllHistory().map { entities -> entities.map(mapper::historyToDomain) }

    override suspend fun getLastHistoryRecords(habitId: Int, limit: Int): List<HabitCompletionHistory> =
        localDataSource.getLastHistoryRecords(habitId, limit).map(mapper::historyToDomain)
}
