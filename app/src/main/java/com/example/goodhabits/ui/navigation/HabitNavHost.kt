package com.example.goodhabits.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goodhabits.viewmodel.HabitUiState
import com.example.goodhabits.viewmodel.HabitViewModel
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.ui.screens.add.AddHabitScreen
import com.example.goodhabits.ui.screens.edit.EditHabitScreen
import com.example.goodhabits.ui.screens.main.MainScreen
import com.example.goodhabits.ui.screens.analytics.AnalyticsScreen
import com.example.goodhabits.ui.screens.settings.SettingsScreen
import com.example.goodhabits.ui.screens.ideas.IdeasScreen
import com.example.goodhabits.ui.screens.ideas.IdeaCategoryDetailScreen
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun HabitApp(viewModel: HabitViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()

    HabitNavHost(
        navController = navController,
        state = state,
        viewModel = viewModel,
        onOpenAddHabit = { 
            viewModel.clearDraft()
            navController.navigate(Screen.AddHabit.route) 
        },
        onOpenEditHabit = { habit -> 
            viewModel.clearDraft()
            navController.navigate(Screen.EditHabit.createRoute(habit.id))
        },
        onOpenAnalytics = { navController.navigate(Screen.Analytics.route) },
        onOpenSettings = { navController.navigate(Screen.Settings.route) },
        onOpenIdeas = { navController.navigate(Screen.Ideas.route) },
        onBackToMain = { navController.popBackStack() },
        onAddHabit = { viewModel.addHabit() },
        onUpdateHabit = { id, title, color, days, isReminderEnabled, motivation -> 
            viewModel.updateHabit(id, title, color, days, isReminderEnabled, motivation) 
        },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onToggleHabitToday = { habitId -> viewModel.toggleHabitToday(habitId) },
        onToggleHabitForDate = { habitId, date -> viewModel.toggleHabitForDate(habitId, date) }
    )
}

@Composable
fun HabitNavHost(
    navController: NavHostController,
    state: HabitUiState,
    viewModel: HabitViewModel,
    onOpenAddHabit: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenIdeas: () -> Unit,
    onOpenEditHabit: (Habit) -> Unit,
    onBackToMain: () -> Unit,
    onAddHabit: () -> Unit,
    onUpdateHabit: (Int, String, Color, Set<String>, Boolean, String) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onToggleHabitToday: (Int) -> Unit,
    onToggleHabitForDate: (Int, LocalDate) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                habits = state.habits,
                currentStreak = state.currentStreak,
                timeSuggestions = state.timeSuggestions,
                dismissedSuggestions = state.dismissedSuggestions,
                onDismissSuggestion = { habitId -> viewModel.dismissSuggestion(habitId) },
                onApplySuggestion = { suggestion -> 
                    val habit = state.habits.find { it.id == suggestion.habitId }
                    if (habit != null) {
                        viewModel.updateHabit(
                            id = habit.id,
                            title = habit.title,
                            color = Color(habit.colorHex.toInt()),
                            days = habit.days,
                            isReminderEnabled = true,
                            time = suggestion.suggestedTime
                        )
                    }
                    viewModel.dismissSuggestion(suggestion.habitId)
                },
                onOpenAddHabit = onOpenAddHabit,
                onOpenAnalytics = onOpenAnalytics,
                onOpenSettings = onOpenSettings,
                onOpenIdeas = onOpenIdeas,
                onToggleHabitToday = onToggleHabitToday,
                onToggleHabitForDate = onToggleHabitForDate,
                onHabitClick = onOpenEditHabit
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onBack = onBackToMain
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = onBackToMain
            )
        }

        composable(Screen.Ideas.route) {
            IdeasScreen(
                onBack = onBackToMain,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.IdeaCategoryDetail.createRoute(categoryId))
                },
                onCreateOwnClick = {
                    onOpenAddHabit()
                }
            )
        }

        composable(
            route = Screen.IdeaCategoryDetail.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            IdeaCategoryDetailScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onIdeaClick = { ideaTitle ->
                    navController.navigate(Screen.AddHabit.createRoute(ideaTitle))
                }
            )
        }

        composable(
            route = Screen.AddHabit.route,
            arguments = listOf(
                navArgument("title") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("categoryId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val preFilledTitle = backStackEntry.arguments?.getString("title")
            AddHabitScreen(
                onBack = onBackToMain,
                onSaveHabit = {
                    onAddHabit()
                    onBackToMain()
                },
                viewModel = viewModel,
                preFilledTitle = preFilledTitle
            )
        }

        composable(
            route = Screen.EditHabit.route,
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: -1
            val habit = state.habits.find { it.id == habitId }

            if (habit == null) {
                onBackToMain()
            } else {
                EditHabitScreen(
                    habit = habit,
                    onBack = onBackToMain,
                    onSaveHabit = { id, title, color, days, time, motivation ->
                        onUpdateHabit(id, title, color, days, time, motivation)
                        onBackToMain()
                    },
                    onDeleteHabit = { id ->
                        onDeleteHabit(id)
                        onBackToMain()
                    },
                    viewModel = viewModel,
                )
            }
        }
    }
}
