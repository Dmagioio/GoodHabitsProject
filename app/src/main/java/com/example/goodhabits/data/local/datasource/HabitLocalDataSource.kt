package com.example.goodhabits.data.local.datasource

import com.example.goodhabits.data.local.dao.HabitCompletionHistoryDao
import com.example.goodhabits.data.local.dao.HabitDao
import com.example.goodhabits.data.local.entity.HabitCompletionHistoryEntity
import com.example.goodhabits.data.local.entity.HabitEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class HabitLocalDataSource @Inject constructor(
    private val habitDao: HabitDao,
    private val historyDao: HabitCompletionHistoryDao
) {
    fun observeHabits(): Flow<List<HabitEntity>> = habitDao.observeHabits()

    suspend fun getHabits(): List<HabitEntity> = habitDao.getHabits()

    suspend fun getHabitById(id: Int): HabitEntity? = habitDao.getHabitById(id)

    suspend fun deleteAll() {
        habitDao.deleteAll()
        historyDao.deleteAll()
    }

    suspend fun insertHabit(habit: HabitEntity) = habitDao.insertHabit(habit).toInt()

    suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)

    suspend fun deleteHabitById(id: Int) = habitDao.deleteHabitById(id)

    suspend fun insertHistory(history: HabitCompletionHistoryEntity) = historyDao.insertHistory(history)

    fun getHistoryForHabit(habitId: Int): Flow<List<HabitCompletionHistoryEntity>> = historyDao.getHistoryForHabit(habitId)

    suspend fun getLastHistoryRecords(habitId: Int, limit: Int): List<HabitCompletionHistoryEntity> = 
        historyDao.getLastHistoryRecords(habitId, limit)

    fun getAllHistory(): Flow<List<HabitCompletionHistoryEntity>> = historyDao.getAllHistory()
}
