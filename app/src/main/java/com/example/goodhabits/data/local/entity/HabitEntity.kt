package com.example.goodhabits.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val colorHex: Long,
    val completedDates: Set<Long> = emptySet(),
    val days: Set<String> = emptySet(),
    val reminderTime: java.time.LocalTime? = null,
)
