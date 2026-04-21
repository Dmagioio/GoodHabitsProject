package com.example.goodhabits.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AddHabit : Screen("add_habit")
    object Analytics : Screen("analytics")
    object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Int) = "edit_habit/$habitId"
    }
}
