package com.example.goodhabits.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goodhabits.HabitUiState
import com.example.goodhabits.HabitViewModel
import com.example.goodhabits.data.Habit
import com.example.goodhabits.ui.screens.add.AddHabitScreen
import com.example.goodhabits.ui.screens.edit.EditHabitScreen
import com.example.goodhabits.ui.screens.main.MainScreen
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
@Composable
fun HabitApp(viewModel: HabitViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    HabitNavHost(
        state = state,
        viewModel = viewModel,
        onOpenAddHabit = { viewModel.openAddHabit() },
        onOpenEditHabit = { habit -> viewModel.openEditHabit(habit) },
        onBackToMain = { viewModel.backToMain() },
        onAddHabit = { title, color, days -> viewModel.addHabit(title, color, days) },
        onUpdateHabit = { id, title, color, days -> viewModel.updateHabit(id, title, color, days) },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onToggleHabitToday = { habitId -> viewModel.toggleHabitToday(habitId) },
        onToggleHabitForDate = { habitId, date -> viewModel.toggleHabitForDate(habitId, date) }
    )
}

@Composable
fun HabitNavHost(
    state: HabitUiState,
    viewModel: HabitViewModel,
    onOpenAddHabit: () -> Unit,
    onOpenEditHabit: (Habit) -> Unit,
    onBackToMain: () -> Unit,
    onAddHabit: (String, Color, Set<String>) -> Unit,
    onUpdateHabit: (Int, String, Color, Set<String>) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onToggleHabitToday: (Int) -> Unit,
    onToggleHabitForDate: (Int, LocalDate) -> Unit
) {
    when (state.currentScreen) {
        RootScreen.Main -> MainScreen(
            habits = state.habits,
            onOpenAddHabit = onOpenAddHabit,
            onToggleHabitToday = onToggleHabitToday,
            onToggleHabitForDate = onToggleHabitForDate,
            onHabitClick = onOpenEditHabit
        )

        RootScreen.AddHabit -> AddHabitScreen(
            onBack = onBackToMain,
            onSaveHabit = onAddHabit,
            viewModel = viewModel
        )

        RootScreen.EditHabit -> {
            val habit = state.habitToEdit
            if (habit == null) {
                onBackToMain()
            } else {
                EditHabitScreen(
                    habit = habit,
                    onBack = onBackToMain,
                    onSaveHabit = onUpdateHabit,
                    onDeleteHabit = onDeleteHabit,

                )
            }
        }
    }
}

