package com.example.goodhabits.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.goodhabits.data.local.converter.HabitTypeConverters
import com.example.goodhabits.data.local.dao.HabitCompletionHistoryDao
import com.example.goodhabits.data.local.dao.HabitDao
import com.example.goodhabits.data.local.entity.HabitCompletionHistoryEntity
import com.example.goodhabits.data.local.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCompletionHistoryEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(HabitTypeConverters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionHistoryDao(): HabitCompletionHistoryDao
}
