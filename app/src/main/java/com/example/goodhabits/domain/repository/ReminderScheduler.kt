package com.example.goodhabits.domain.repository

import java.time.LocalTime

interface ReminderScheduler {
    fun schedule(habitId: Int, habitTitle: String, time: LocalTime)

    fun cancel(habitId: Int)
}
