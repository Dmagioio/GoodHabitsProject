package com.example.goodhabits.data

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/**
 * Простий in-memory репозиторій для звичок.
 * З часом його можна замінити на Room / мережу тощо.
 */
class HabitRepository {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private var nextHabitId: Int = 0

    fun addHabit(title: String, color: Color) {
        val newHabit = Habit(
            id = nextHabitId++,
            title = title,
            color = color
        )
        _habits.update { it + newHabit }
    }

    fun updateHabit(id: Int, title: String, color: Color) {
        _habits.update { list ->
            list.map { habit ->
                if (habit.id == id) habit.copy(title = title, color = color) else habit
            }
        }
    }

    fun deleteHabit(id: Int) {
        _habits.update { list -> list.filterNot { it.id == id } }
    }

    fun toggleHabitForDate(id: Int, date: LocalDate) {
        val key = date.toEpochDay()
        _habits.update { list ->
            list.map { habit ->
                if (habit.id == id) {
                    val updatedDates = habit.completedDates.toMutableSet()
                    if (!updatedDates.add(key)) {
                        updatedDates.remove(key)
                    }
                    habit.copy(completedDates = updatedDates)
                } else {
                    habit
                }
            }
        }
    }
}

