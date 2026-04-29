package com.example.goodhabits.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.R
import com.example.goodhabits.domain.analysis.BehavioralAnalysisEngine
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.usecase.ObserveHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

enum class AnalyticsPeriod {
    Weekly, Monthly
}

data class AnalyticsUiState(
    val habits: List<Habit> = emptyList(),
    val selectedHabitId: Int? = null,
    val period: AnalyticsPeriod = AnalyticsPeriod.Weekly,
    val currentMonth: YearMonth = YearMonth.now(),
    val successRate: Float = 0f,
    val trend: Float = 0f,
    val heatmapData: Map<LocalDate, Float> = emptyMap(),
    val weeklyData: List<Pair<LocalDate, Float>> = emptyList(),
    val monthlyTrendData: List<Pair<LocalDate, Float>> = emptyList(),
    val bestDayOfWeek: DayOfWeek? = null,
    val totalCompletions: Int = 0,
    @StringRes val insightDayRes: Int? = null
) {
    val filteredHabits: List<Habit> = if (selectedHabitId == null) habits else habits.filter { it.id == selectedHabitId }
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val observeHabitsUseCase: ObserveHabitsUseCase,
    private val analysisEngine: BehavioralAnalysisEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeHabitsUseCase().collect { habits ->
                _uiState.update { it.copy(habits = habits) }
                refreshAnalytics()
            }
        }
    }

    fun selectHabit(habitId: Int?) {
        _uiState.update { it.copy(selectedHabitId = habitId) }
        refreshAnalytics()
    }

    fun togglePeriod() {
        _uiState.update { 
            it.copy(period = if (it.period == AnalyticsPeriod.Weekly) AnalyticsPeriod.Monthly else AnalyticsPeriod.Weekly) 
        }
        refreshAnalytics()
    }

    fun previousMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.minusMonths(1)) }
        refreshAnalytics()
    }

    fun nextMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.plusMonths(1)) }
        refreshAnalytics()
    }

    private fun refreshAnalytics() {
        val state = _uiState.value
        val habits = state.filteredHabits
        if (habits.isEmpty()) return

        val today = LocalDate.now()
        
        viewModelScope.launch {
            val currentPeriodStart = today.minusDays(6)
            val currentSuccessRate = calculateSuccessRate(habits, currentPeriodStart, today)
            
            val previousPeriodEnd = currentPeriodStart.minusDays(1)
            val previousPeriodStart = previousPeriodEnd.minusDays(6)
            val previousSuccessRate = calculateSuccessRate(habits, previousPeriodStart, previousPeriodEnd)
            
            val trend = currentSuccessRate - previousSuccessRate

            val weeklyData = (6 downTo 0).map { i ->
                val date = today.minusDays(i.toLong())
                date to calculateSuccessRate(habits, date, date)
            }

            val monthlyTrendData = (29 downTo 0).map { i ->
                val date = today.minusDays(i.toLong())
                date to calculateSuccessRate(habits, date, date)
            }

            val monthStart = state.currentMonth.atDay(1)
            val monthEnd = state.currentMonth.atEndOfMonth()
            val heatmapData = mutableMapOf<LocalDate, Float>()
            var curr = monthStart
            while (!curr.isAfter(monthEnd)) {
                heatmapData[curr] = calculateSuccessRate(habits, curr, curr)
                curr = curr.plusDays(1)
            }

            val dayPlanned = DayOfWeek.values().associateWith { 0 }.toMutableMap()
            val dayActual = DayOfWeek.values().associateWith { 0 }.toMutableMap()
            
            val analysisStart = today.minusMonths(3)
            var analysisCurr = analysisStart
            while (!analysisCurr.isAfter(today)) {
                val dayOfWeek = analysisCurr.dayOfWeek
                val internalDay = when (dayOfWeek) {
                    DayOfWeek.MONDAY -> "MO"
                    DayOfWeek.TUESDAY -> "TU"
                    DayOfWeek.WEDNESDAY -> "WE"
                    DayOfWeek.THURSDAY -> "TH"
                    DayOfWeek.FRIDAY -> "FR"
                    DayOfWeek.SATURDAY -> "SA"
                    DayOfWeek.SUNDAY -> "SU"
                }
                habits.forEach { habit ->
                    if (habit.days.contains(internalDay)) {
                        dayPlanned[dayOfWeek] = dayPlanned[dayOfWeek]!! + 1
                        if (habit.completedDates.contains(analysisCurr.toEpochDay())) {
                            dayActual[dayOfWeek] = dayActual[dayOfWeek]!! + 1
                        }
                    }
                }
                analysisCurr = analysisCurr.plusDays(1)
            }

            val bestDay = DayOfWeek.values()
                .filter { dayPlanned[it]!! > 0 }
                .maxByOrNull { day ->
                    val rate = dayActual[day]!!.toFloat() / dayPlanned[day]!!
                    rate * 10000 + dayActual[day]!!
                }

            var total = 0
            habits.forEach { total += it.completedDates.size }

            val weakestDay = analysisEngine.analyzeWeakestDay(state.selectedHabitId)
            val insightDayRes = weakestDay?.let {
                when (it) {
                    DayOfWeek.MONDAY -> R.string.day_monday_acc
                    DayOfWeek.TUESDAY -> R.string.day_tuesday_acc
                    DayOfWeek.WEDNESDAY -> R.string.day_wednesday_acc
                    DayOfWeek.THURSDAY -> R.string.day_thursday_acc
                    DayOfWeek.FRIDAY -> R.string.day_friday_acc
                    DayOfWeek.SATURDAY -> R.string.day_saturday_acc
                    DayOfWeek.SUNDAY -> R.string.day_sunday_acc
                }
            }

            _uiState.update { 
                it.copy(
                    successRate = currentSuccessRate,
                    trend = trend,
                    weeklyData = weeklyData,
                    monthlyTrendData = monthlyTrendData,
                    heatmapData = heatmapData,
                    bestDayOfWeek = bestDay,
                    totalCompletions = total,
                    insightDayRes = insightDayRes
                )
            }
        }
    }

    private fun calculateSuccessRate(habits: List<Habit>, start: LocalDate, end: LocalDate): Float {
        var planned = 0
        var actual = 0
        
        var curr = start
        while (!curr.isAfter(end)) {
            val dayOfWeek = when (curr.dayOfWeek) {
                DayOfWeek.MONDAY -> "MO"
                DayOfWeek.TUESDAY -> "TU"
                DayOfWeek.WEDNESDAY -> "WE"
                DayOfWeek.THURSDAY -> "TH"
                DayOfWeek.FRIDAY -> "FR"
                DayOfWeek.SATURDAY -> "SA"
                DayOfWeek.SUNDAY -> "SU"
            }
            
            habits.forEach { habit ->
                if (habit.days.contains(dayOfWeek)) {
                    planned++
                    if (habit.completedDates.contains(curr.toEpochDay())) {
                        actual++
                    }
                }
            }
            curr = curr.plusDays(1)
        }
        
        return if (planned == 0) 0f else actual.toFloat() / planned
    }
}
