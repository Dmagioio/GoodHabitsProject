package com.example.goodhabits.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalTime

class HabitTypeConverters {
    @TypeConverter
    fun fromCompletedDates(value: Set<Long>): String = value.joinToString(",")

    @TypeConverter
    fun toCompletedDates(value: String): Set<Long> =
        if (value.isBlank()) emptySet()
        else value.split(",").map { it.toLong() }.toSet()

    @TypeConverter
    fun fromDays(value: Set<String>): String = value.joinToString(",")

    @TypeConverter
    fun toDays(value: String): Set<String> =
        if (value.isBlank()) emptySet()
        else value.split(",").toSet()

    @TypeConverter
    fun fromReminderTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toReminderTime(value: String?): LocalTime? = value?.takeIf { it.isNotBlank() }?.let(LocalTime::parse)

    @TypeConverter
    fun fromLocalDate(value: java.time.LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): java.time.LocalDate? = value?.takeIf { it.isNotBlank() }?.let(java.time.LocalDate::parse)
}
