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
        // Look at the last 14 days of history to find a more stable pattern
        val history = repository.getLastHistoryRecords(habitId, 14)
        
        // Ensure we have completions on at least 5 different days to avoid "rapid-fire" clicks
        val distinctDaysHistory = history.groupBy { it.date }.map { it.value.first() }
        if (distinctDaysHistory.size < 5) return null

        val habit = repository.getHabitById(habitId) ?: return null
        val plannedTime = habit.reminderTime ?: return null

        // Sliding window approach: find the 30-minute window that contains the most completions
        val timesInMinutes = distinctDaysHistory.map { it.actualTime.hour * 60 + it.actualTime.minute }.sorted()
        
        var maxCount = 0
        var bestStartTime = -1

        for (i in timesInMinutes.indices) {
            val startTime = timesInMinutes[i]
            val endTime = startTime + 30
            
            // Count how many completions fall into [startTime, startTime + 30]
            val count = timesInMinutes.count { it in startTime..endTime }
            
            if (count > maxCount) {
                maxCount = count
                bestStartTime = startTime
            }
        }

        if (bestStartTime == -1) return null
            
        val frequency = maxCount
        
        // Suggest change if the same 30-min window is used in at least 60% of tracked days (min 5 days)
        val stabilityThreshold = (distinctDaysHistory.size * 0.6).toInt().coerceAtLeast(4)
        
        if (frequency >= stabilityThreshold) {
            // Calculate the average time within this best window for a more precise suggestion
            val windowStartTime = bestStartTime
            val windowEndTime = bestStartTime + 30
            val timesInWindow = timesInMinutes.filter { it in windowStartTime..windowEndTime }
            val averageMinutes = timesInWindow.average().toInt()
            
            val suggestedTime = LocalTime.of(averageMinutes / 60, averageMinutes % 60)
            
            // Only suggest if it's different from planned time (more than 30 mins difference)
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

        // Get history for all habits
        val allHistory = mutableListOf<HabitCompletionHistory>()
        habits.forEach { habit ->
            allHistory.addAll(repository.getLastHistoryRecords(habit.id, 10))
        }

        if (allHistory.isEmpty()) return emptyList()

        // Group by date
        val historyByDate = allHistory.groupBy { it.date }
        
        val pairCorrelations = mutableMapOf<Pair<Int, Int>, MutableList<Long>>()

        historyByDate.values.forEach { dayHistory ->
            val sortedDayHistory = dayHistory.sortedBy { it.actualTime }
            
            for (i in 0 until sortedDayHistory.size - 1) {
                val first = sortedDayHistory[i]
                val second = sortedDayHistory[i + 1]
                
                val delay = Duration.between(first.actualTime, second.actualTime).toMinutes()
                if (delay in 1..15) { // If second habit is within 15 mins of first
                    val pair = first.habitId to second.habitId
                    pairCorrelations.getOrPut(pair) { mutableListOf() }.add(delay)
                }
            }
        }

        return pairCorrelations.filter { it.value.size >= 3 } // If seen at least 3 times
            .map { (pair, delays) ->
                HabitStackingChain(
                    firstHabitId = pair.first,
                    secondHabitId = pair.second,
                    averageDelayMinutes = delays.average().toLong()
                )
            }
    }
}
