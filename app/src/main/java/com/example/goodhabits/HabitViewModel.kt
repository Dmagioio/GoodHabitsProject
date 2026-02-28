package com.example.goodhabits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.goodhabits.data.Habit
import com.example.goodhabits.data.HabitRepository
import com.example.goodhabits.ui.navigation.RootScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.String

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val currentScreen: RootScreen = RootScreen.Main,
    val habitToEdit: Habit? = null
)
class HabitViewModel(
    private val repository: HabitRepository = HabitRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState

    private val _reminderTime = MutableStateFlow(LocalTime.of(12, 0))
    val reminderTime: StateFlow<LocalTime> = _reminderTime.asStateFlow()

    init {
        viewModelScope.launch {
            repository.habits.collect { list ->
                _uiState.update { state -> state.copy(habits = list) }
            }
        }
    }

    fun openAddHabit() {
        _uiState.update { it.copy(currentScreen = RootScreen.AddHabit) }
    }

    fun openEditHabit(habit: Habit) {
        _uiState.update { it.copy(currentScreen = RootScreen.EditHabit, habitToEdit = habit) }
    }

    fun backToMain() {
        _uiState.update { it.copy(currentScreen = RootScreen.Main, habitToEdit = null) }
    }

    fun addHabit(title: String, color: Color, days: Set<String>) {
        repository.addHabit(
            title = title,
            colorHex = color.toArgb().toLong(),
            days = days
        )
        backToMain()
    }

    fun updateHabit(id: Int, title: String, color: Color, days: Set<String>) {
        repository.updateHabit(
            id = id,
            title = title,
            colorHex = color.toArgb().toLong(),
            days = days
        )
        backToMain()
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _reminderTime.value = LocalTime.of(hour, minute)
    }

    fun deleteHabit(id: Int) {
        repository.deleteHabit(id)
        backToMain()
    }

    fun toggleHabitToday(habitId: Int) {
        repository.toggleHabitForDate(habitId, LocalDate.now())
    }

    fun toggleHabitForDate(habitId: Int, date: LocalDate) {
        repository.toggleHabitForDate(habitId, date)
    }
}