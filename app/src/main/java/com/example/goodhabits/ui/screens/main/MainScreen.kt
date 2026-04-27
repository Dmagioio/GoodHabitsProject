package com.example.goodhabits.ui.screens.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.res.stringResource
import com.example.goodhabits.R
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.ui.components.EmptyHabitsContent
import com.example.goodhabits.ui.theme.GreyBlue
import com.example.goodhabits.ui.theme.Purple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.goodhabits.domain.analysis.TimeAdaptationSuggestion

enum class TabScreen(val titleRes: Int) {
    Daily(R.string.daily_tab),
    Weekly(R.string.weekly_tab),
    Overall(R.string.overall_tab)
}

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.streak_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (streak > 0) 
                        stringResource(R.string.streak_label) 
                    else 
                        stringResource(R.string.streak_start_msg),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                
                Column {
                    Text(
                        text = stringResource(R.string.streak_days, streak),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        )
                    )
                    Text(
                        text = stringResource(R.string.streak_label),
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    habits: List<Habit>,
    currentStreak: Int = 0,
    timeSuggestions: Map<Int, TimeAdaptationSuggestion> = emptyMap(),
    dismissedSuggestions: Set<Int> = emptySet(),
    onDismissSuggestion: (Int) -> Unit = {},
    onApplySuggestion: (TimeAdaptationSuggestion) -> Unit = {},
    onOpenAddHabit: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenIdeas: () -> Unit,
    onToggleHabitToday: (Int) -> Unit,
    onToggleHabitForDate: (Int, LocalDate) -> Unit,
    onHabitClick: (Habit) -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var gesturesEnabled by remember { mutableStateOf(false) }

    val pendingSuggestions = timeSuggestions.filter { (id, _) -> !dismissedSuggestions.contains(id) }.values.toList()
    var currentSuggestionToShow by remember { mutableStateOf<TimeAdaptationSuggestion?>(null) }

    LaunchedEffect(pendingSuggestions) {
        if (pendingSuggestions.isNotEmpty() && currentSuggestionToShow == null) {
            currentSuggestionToShow = pendingSuggestions.first()
        }
    }

    if (currentSuggestionToShow != null) {
        val suggestion = currentSuggestionToShow!!
        val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
        
        AlertDialog(
            onDismissRequest = { 
                onDismissSuggestion(suggestion.habitId)
                currentSuggestionToShow = null 
            },
            title = { Text("Оптимізація часу") },
            text = { 
                Text("Я бачу, ти зазвичай виконуєш звичку \"${suggestion.habitTitle}\" о ${suggestion.suggestedTime.format(timeFormatter)}. Перенести нагадування з ${suggestion.currentPlannedTime?.format(timeFormatter) ?: "--:--"} на цей час?") 
            },
            confirmButton = {
                TextButton(onClick = {
                    onApplySuggestion(suggestion)
                    currentSuggestionToShow = null
                }) {
                    Text("Так, перенести")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissSuggestion(suggestion.habitId)
                    currentSuggestionToShow = null
                }) {
                    Text("Ні, дякую")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        delay(150)
        gesturesEnabled = true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.habits_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StreakCard(streak = currentStreak)

                    Spacer(modifier = Modifier.height(16.dp))

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.ideas)) },
                        icon = { Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null) },
                        selected = false,
                        onClick = { 
                            scope.launch { 
                                drawerState.snapTo(androidx.compose.material3.DrawerValue.Closed)
                                onOpenIdeas()
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
                                drawerState.snapTo(androidx.compose.material3.DrawerValue.Closed)
                                onOpenAnalytics()
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
                                drawerState.snapTo(androidx.compose.material3.DrawerValue.Closed)
                                onOpenSettings()
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
        gesturesEnabled = gesturesEnabled
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.habits_title)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (gesturesEnabled) {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                }
                            },
                            enabled = gesturesEnabled
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenAddHabit) {
                            Text(
                                text = "+",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.4f
                                )
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (habits.isEmpty()) {
                    EmptyHabitsContent(
                        onCreateClick = onOpenAddHabit,
                        onIdeasClick = onOpenIdeas
                    )
                } else {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = GreyBlue)
                            .padding(4.dp),
                        containerColor = Color.Transparent,
                        indicator = { },
                        divider = { }
                    ) {
                        TabScreen.entries.forEachIndexed { index, screen ->
                            val selected = selectedTabIndex == index
                            Tab(
                                selected = selected,
                                onClick = { selectedTabIndex = index },
                                selectedContentColor = Color.White,
                                unselectedContentColor = Color.DarkGray,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) Purple else Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(screen.titleRes),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.zIndex(1f)
                                    )
                                }
                            }
                        }
                    }
                    Crossfade(
                        targetState = selectedTabIndex,
                        label = "TabContentCrossfade"
                    ) { targetIndex ->
                        when (TabScreen.entries[targetIndex]) {
                            TabScreen.Daily -> DailyScreen(
                                habits = habits,
                                timeSuggestions = timeSuggestions,
                                onToggleHabitToday = { habit ->
                                    onToggleHabitToday(habit.id)
                                },
                                onHabitClick = onHabitClick
                            )

                            TabScreen.Weekly -> WeeklyScreen(
                                habits = habits,
                                onToggleHabitForDate = { habit, date ->
                                    onToggleHabitForDate(habit.id, date)
                                },
                                onHabitClick = onHabitClick
                            )

                            TabScreen.Overall -> OverallScreen(
                                habits = habits,
                                onToggleHabitToday = { habit ->
                                    onToggleHabitToday(habit.id)
                                },
                                onHabitClick = onHabitClick
                            )
                        }
                    }
                }
            }
        }
    }
}

