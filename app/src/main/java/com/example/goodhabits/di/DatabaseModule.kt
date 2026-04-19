package com.example.goodhabits.di

import android.content.Context
import androidx.room.Room
import com.example.goodhabits.data.local.dao.HabitDao
import com.example.goodhabits.data.local.database.HabitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
    ).build()

    @Provides
    fun provideHabitDao(database: HabitDatabase): HabitDao = database.habitDao()
}
