package com.example.goodhabits

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.example.goodhabits.data.Habit
import com.example.goodhabits.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

enum class RootScreen {
    Main,
    AddHabit,
    EditHabit
}

/**
 * ViewModel – зберігає стан і бізнес-логіку додатку.
 */
class HabitViewModel(
    private val repository: HabitRepository = HabitRepository()
) : ViewModel() {

    // Поточний список звичок, який спостерігає UI.
    val habits: StateFlow<List<Habit>> =
        repository.habits.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Поточний екран (головний / створення / редагування)
    var currentRootScreen by mutableStateOf(RootScreen.Main)
        private set

    // Поточна звичка для редагування (якщо є)
    var habitToEdit by mutableStateOf<Habit?>(null)
        private set

    fun openAddHabit() {
        currentRootScreen = RootScreen.AddHabit
    }

    fun openEditHabit(habit: Habit) {
        habitToEdit = habit
        currentRootScreen = RootScreen.EditHabit
    }

    fun backToMain() {
        currentRootScreen = RootScreen.Main
        habitToEdit = null
    }

    fun addHabit(title: String, color: Color) {
        repository.addHabit(title, color)
        currentRootScreen = RootScreen.Main
    }

    fun updateHabit(id: Int, title: String, color: Color) {
        repository.updateHabit(id, title, color)
        currentRootScreen = RootScreen.Main
    }

    fun deleteHabit(id: Int) {
        repository.deleteHabit(id)
        currentRootScreen = RootScreen.Main
    }

    fun toggleHabitToday(habitId: Int) {
        repository.toggleHabitForDate(habitId, LocalDate.now())
    }

    fun toggleHabitForDate(habitId: Int, date: LocalDate) {
        repository.toggleHabitForDate(habitId, date)
    }
}

