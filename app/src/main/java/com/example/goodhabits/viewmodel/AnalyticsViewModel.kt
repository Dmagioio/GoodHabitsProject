package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

enum class AnalyticsPeriod {
    Weekly, Monthly
}

data class AnalyticsUiState(
    val habits: List<Habit> = emptyList(),
    val selectedHabitId: Int? = null, // null means "All habits"
    val period: AnalyticsPeriod = AnalyticsPeriod.Weekly,
    val currentMonth: YearMonth = YearMonth.now()
) {
    val filteredHabits: List<Habit> = if (selectedHabitId == null) habits else habits.filter { it.id == selectedHabitId }
    val allCompletedDates: Set<Long> = filteredHabits.flatMap { it.completedDates }.toSet()
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val observeHabitsUseCase: ObserveHabitsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeHabitsUseCase().collect { habits ->
                _uiState.update { it.copy(habits = habits) }
            }
        }
    }

    fun selectHabit(habitId: Int?) {
        _uiState.update { it.copy(selectedHabitId = habitId) }
    }

    fun togglePeriod() {
        _uiState.update { 
            it.copy(period = if (it.period == AnalyticsPeriod.Weekly) AnalyticsPeriod.Monthly else AnalyticsPeriod.Weekly) 
        }
    }

    fun previousMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.minusMonths(1)) }
    }

    fun nextMonth() {
        _uiState.update { it.copy(currentMonth = it.currentMonth.plusMonths(1)) }
    }

    fun getCompletionsPerDayOfWeek(): Map<DayOfWeek, Int> {
        val stats = mutableMapOf<DayOfWeek, Int>()
        uiState.value.filteredHabits.forEach { habit ->
            habit.completedDates.forEach { epochDay ->
                val date = LocalDate.ofEpochDay(epochDay)
                val dayOfWeek = date.dayOfWeek
                stats[dayOfWeek] = (stats[dayOfWeek] ?: 0) + 1
            }
        }
        return stats
    }

    fun getCompletionsPerDayOfMonth(): Map<Int, Int> {
        val stats = mutableMapOf<Int, Int>()
        val state = uiState.value
        state.filteredHabits.forEach { habit ->
            habit.completedDates.forEach { epochDay ->
                val date = LocalDate.ofEpochDay(epochDay)
                if (YearMonth.from(date) == state.currentMonth) {
                    val dayOfMonth = date.dayOfMonth
                    stats[dayOfMonth] = (stats[dayOfMonth] ?: 0) + 1
                }
            }
        }
        return stats
    }
}
