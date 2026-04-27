package com.example.goodhabits.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goodhabits.data.local.entity.HabitCompletionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HabitCompletionHistoryEntity)

    @Query("SELECT * FROM habit_completion_history WHERE habitId = :habitId ORDER BY date DESC, actualTime DESC")
    fun getHistoryForHabit(habitId: Int): Flow<List<HabitCompletionHistoryEntity>>

    @Query("SELECT * FROM habit_completion_history WHERE habitId = :habitId ORDER BY date DESC, actualTime DESC LIMIT :limit")
    suspend fun getLastHistoryRecords(habitId: Int, limit: Int): List<HabitCompletionHistoryEntity>

    @Query("SELECT * FROM habit_completion_history ORDER BY date DESC, actualTime DESC")
    fun getAllHistory(): Flow<List<HabitCompletionHistoryEntity>>
    
    @Query("DELETE FROM habit_completion_history")
    suspend fun deleteAll()
}
