package com.example.goodhabits.domain.analysis

import com.example.goodhabits.domain.model.HabitCompletionHistory
import com.example.goodhabits.domain.repository.HabitRepository
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

data class TimeAdaptationSuggestion(
    val habitId: Int,
    val habitTitle: String,
    val currentPlannedTime: LocalTime?,
    val suggestedTime: LocalTime
)

data class HabitStackingChain(
    val firstHabitId: Int,
    val secondHabitId: Int,
    val averageDelayMinutes: Long
)

@Singleton
class BehavioralAnalysisEngine @Inject constructor(
    private val repository: HabitRepository
) {
    suspend fun analyzeTimeAdaptation(habitId: Int): TimeAdaptationSuggestion? {
        val history = repository.getLastHistoryRecords(habitId, 14)
        
        val distinctDaysHistory = history.groupBy { it.date }.map { it.value.first() }
        if (distinctDaysHistory.size < 5) return null

        val habit = repository.getHabitById(habitId) ?: return null
        val plannedTime = habit.reminderTime ?: return null

        val timesInMinutes = distinctDaysHistory.map { it.actualTime.hour * 60 + it.actualTime.minute }.sorted()
        
        var maxCount = 0
        var bestStartTime = -1

        for (i in timesInMinutes.indices) {
            val startTime = timesInMinutes[i]
            val endTime = startTime + 30
            
            val count = timesInMinutes.count { it in startTime..endTime }
            
            if (count > maxCount) {
                maxCount = count
                bestStartTime = startTime
            }
        }

        if (bestStartTime == -1) return null
            
        val frequency = maxCount
        
        val stabilityThreshold = (distinctDaysHistory.size * 0.6).toInt().coerceAtLeast(4)
        
        if (frequency >= stabilityThreshold) {
            val windowStartTime = bestStartTime
            val windowEndTime = bestStartTime + 30
            val timesInWindow = timesInMinutes.filter { it in windowStartTime..windowEndTime }
            val averageMinutes = timesInWindow.average().toInt()
            
            val suggestedTime = LocalTime.of(averageMinutes / 60, averageMinutes % 60)
            
            val diff = Duration.between(plannedTime, suggestedTime).abs().toMinutes()
            if (diff >= 30) {
                return TimeAdaptationSuggestion(
                    habitId = habitId,
                    habitTitle = habit.title,
                    currentPlannedTime = plannedTime,
                    suggestedTime = suggestedTime
                )
            }
        }
        
        return null
    }

    suspend fun findHabitChains(): List<HabitStackingChain> {
        val habits = repository.getHabits()
        if (habits.size < 2) return emptyList()

        val allHistory = mutableListOf<HabitCompletionHistory>()
        habits.forEach { habit ->
            allHistory.addAll(repository.getLastHistoryRecords(habit.id, 10))
        }

        if (allHistory.isEmpty()) return emptyList()

        val historyByDate = allHistory.groupBy { it.date }
        
        val pairCorrelations = mutableMapOf<Pair<Int, Int>, MutableList<Long>>()

        historyByDate.values.forEach { dayHistory ->
            val sortedDayHistory = dayHistory.sortedBy { it.actualTime }
            
            for (i in 0 until sortedDayHistory.size - 1) {
                val first = sortedDayHistory[i]
                val second = sortedDayHistory[i + 1]
                
                val delay = Duration.between(first.actualTime, second.actualTime).toMinutes()
                if (delay in 1..15) {
                    val pair = first.habitId to second.habitId
                    pairCorrelations.getOrPut(pair) { mutableListOf() }.add(delay)
                }
            }
        }

        return pairCorrelations.filter { it.value.size >= 3 }
            .map { (pair, delays) ->
                HabitStackingChain(
                    firstHabitId = pair.first,
                    secondHabitId = pair.second,
                    averageDelayMinutes = delays.average().toLong()
                )
            }
    }

    suspend fun analyzeWeakestDay(habitId: Int? = null): java.time.DayOfWeek? {
        val habits = if (habitId != null) {
            repository.getHabitById(habitId)?.let { listOf(it) } ?: emptyList()
        } else {
            repository.getHabits()
        }

        if (habits.isEmpty()) return null

        val dayStats = java.time.DayOfWeek.values().associateWith { 0 to 0 }.toMutableMap()

        val today = java.time.LocalDate.now()
        val startDate = today.minusDays(28)

        var curr = startDate
        while (!curr.isAfter(today)) {
            val dayOfWeek = curr.dayOfWeek
            val internalDay = when (dayOfWeek) {
                java.time.DayOfWeek.MONDAY -> "MO"
                java.time.DayOfWeek.TUESDAY -> "TU"
                java.time.DayOfWeek.WEDNESDAY -> "WE"
                java.time.DayOfWeek.THURSDAY -> "TH"
                java.time.DayOfWeek.FRIDAY -> "FR"
                java.time.DayOfWeek.SATURDAY -> "SA"
                java.time.DayOfWeek.SUNDAY -> "SU"
            }

            habits.forEach { habit ->
                if (habit.days.contains(internalDay)) {
                    val currentStats = dayStats[dayOfWeek]!!
                    val isCompleted = habit.completedDates.contains(curr.toEpochDay())
                    dayStats[dayOfWeek] = (currentStats.first + 1) to (currentStats.second + (if (isCompleted) 1 else 0))
                }
            }
            curr = curr.plusDays(1)
        }

        return dayStats
            .filter { it.value.first > 0 }
            .mapValues { it.value.second.toFloat() / it.value.first }
            .filter { it.value < 0.8f }
            .minByOrNull { it.value }?.key
    }
}
