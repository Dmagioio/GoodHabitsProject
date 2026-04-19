package com.example.goodhabits.di

import com.example.goodhabits.data.repository.HabitRepositoryImpl
import com.example.goodhabits.domain.repository.HabitRepository
import com.example.goodhabits.domain.repository.ReminderScheduler
import com.example.goodhabits.notifications.AlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        repositoryImpl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(
        alarmScheduler: AlarmScheduler
    ): ReminderScheduler
}
