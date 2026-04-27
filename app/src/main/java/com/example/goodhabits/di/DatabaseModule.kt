package com.example.goodhabits.di

import android.content.Context
import androidx.room.Room
import com.example.goodhabits.data.local.dao.HabitDao
import com.example.goodhabits.data.local.database.HabitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.goodhabits.data.local.dao.HabitCompletionHistoryDao
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideHabitDatabase(
        @ApplicationContext context: Context
    ): HabitDatabase = Room.databaseBuilder(
        context,
        HabitDatabase::class.java,
        "good_habits.db"
    )
    .fallbackToDestructiveMigration() // Added to handle database version changes for simplicity in development
    .build()

    @Provides
    fun provideHabitDao(database: HabitDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideHabitCompletionHistoryDao(database: HabitDatabase): HabitCompletionHistoryDao = 
        database.habitCompletionHistoryDao()
}
