package com.example.goodhabits.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AddHabit : Screen("add_habit?title={title}&categoryId={categoryId}") {
        fun createRoute(title: String? = null, categoryId: String? = null): String {
            return when {
                title != null && categoryId != null -> "add_habit?title=$title&categoryId=$categoryId"
                title != null -> "add_habit?title=$title"
                categoryId != null -> "add_habit?categoryId=$categoryId"
                else -> "add_habit"
            }
        }
    }
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object Ideas : Screen("ideas")
    object IdeaCategoryDetail : Screen("idea_category/{categoryId}") {
        fun createRoute(categoryId: String) = "idea_category/$categoryId"
    }
    object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Int) = "edit_habit/$habitId"
    }
}
