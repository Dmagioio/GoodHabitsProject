package com.example.goodhabits.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goodhabits.viewmodel.HabitUiState
import com.example.goodhabits.viewmodel.HabitViewModel
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.ui.screens.add.AddHabitScreen
import com.example.goodhabits.ui.screens.edit.EditHabitScreen
import com.example.goodhabits.ui.screens.main.MainScreen
import com.example.goodhabits.ui.screens.main.StreakCard
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.goodhabits.R
import com.example.goodhabits.ui.theme.Purple
import androidx.compose.ui.layout.ContentScale

@Composable
fun HabitApp(viewModel: HabitViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnMain = currentRoute == Screen.Main.route

    var menuLocked by remember { mutableStateOf(false) }
    val isDrawerEnabled = isOnMain && !menuLocked

    LaunchedEffect(currentRoute, isOnMain) {
        drawerState.snapTo(DrawerValue.Closed)
        menuLocked = true
        if (isOnMain) {
            delay(150)
        }
        menuLocked = false
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = isOnMain && !menuLocked,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp, bottom = 16.dp)
                    ) {
                        StreakCard(streak = state.currentStreak)

                        Spacer(modifier = Modifier.height(12.dp))

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.ideas)) },
                        icon = { Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(Screen.Ideas.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(Screen.Main.route) { saveState = true }
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.analytics)) },
                        icon = { Icon(imageVector = Icons.Filled.ShowChart, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(Screen.Analytics.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(Screen.Main.route) { saveState = true }
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.settings)) },
                        icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(Screen.Settings.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(Screen.Main.route) { saveState = true }
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        ) {
            HabitNavHost(
                navController = navController,
                state = state,
                viewModel = viewModel,
                onToggleDrawer = {
                    if (isDrawerEnabled) {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }
                },
                isDrawerEnabled = isDrawerEnabled,
                onOpenIdeas = {
                    navController.navigate(Screen.Ideas.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Screen.Main.route) { saveState = true }
                    }
                },
                onOpenAddHabit = { 
                    viewModel.clearDraft()
                    navController.navigate(Screen.AddHabit.createRoute()) {
                        launchSingleTop = true
                    }
                },
                onOpenEditHabit = { habit -> 
                    viewModel.clearDraft()
                    navController.navigate(Screen.EditHabit.createRoute(habit.id)) {
                        launchSingleTop = true
                    }
                },
                onBackToMain = { navController.popBackStack(Screen.Main.route, false) },
                onAddHabit = { viewModel.addHabit() },
                onUpdateHabit = { id, title, color, days, isReminderEnabled, motivation -> 
                    viewModel.updateHabit(id, title, color, days, isReminderEnabled, motivation) 
                },
                onDeleteHabit = { id -> viewModel.deleteHabit(id) },
                onToggleHabitToday = { habitId -> viewModel.toggleHabitToday(habitId) },
                onToggleHabitForDate = { habitId, date -> viewModel.toggleHabitForDate(habitId, date) }
            )
        }
    }
}

@Composable
fun HabitNavHost(
    navController: NavHostController,
    state: HabitUiState,
    viewModel: HabitViewModel,
    onToggleDrawer: () -> Unit,
    isDrawerEnabled: Boolean,
    onOpenIdeas: () -> Unit,
    onOpenAddHabit: () -> Unit,
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
                isLoading = state.isLoading,
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
                onToggleDrawer = onToggleDrawer,
                isDrawerEnabled = isDrawerEnabled,
                onOpenAddHabit = onOpenAddHabit,
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
                    navController.navigate(Screen.AddHabit.createRoute(ideaTitle)) {
                        launchSingleTop = true
                    }
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
                LaunchedEffect(habitId, state.habits.size) {
                    delay(250)
                    val stillMissing = state.habits.none { it.id == habitId }
                    if (stillMissing) {
                        navController.popBackStack()
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                EditHabitScreen(
                    habit = habit,
                    onBack = onBackToMain,
                    onSaveHabit = { id, title, color, days, isReminderEnabled, motivation ->
                        onUpdateHabit(id, title, color, days, isReminderEnabled, motivation)
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