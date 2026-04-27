package com.example.goodhabits.domain.repository

import java.time.LocalTime

interface ReminderScheduler {
    fun schedule(habitId: Int, habitTitle: String, time: LocalTime, motivation: String = "")

    fun cancel(habitId: Int)
}
