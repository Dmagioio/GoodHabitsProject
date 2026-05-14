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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.res.painterResource
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.goodhabits.domain.analysis.TimeAdaptationSuggestion
import com.example.goodhabits.ui.theme.SurfaceLevel1
import com.example.goodhabits.ui.theme.SurfaceLevel2

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.45f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (streak > 0) 
                        stringResource(R.string.streak_label_all)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.streak_days, streak),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp
                            )
                        )
                        if (streak > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔥",
                                fontSize = 32.sp
                            )
                        }
                    }
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
    isLoading: Boolean = false,
    currentStreak: Int = 0,
    timeSuggestions: Map<Int, TimeAdaptationSuggestion> = emptyMap(),
    dismissedSuggestions: Set<Int> = emptySet(),
    onDismissSuggestion: (Int) -> Unit = {},
    onApplySuggestion: (TimeAdaptationSuggestion) -> Unit = {},
    onToggleDrawer: () -> Unit,
    isDrawerEnabled: Boolean,
    onOpenAddHabit: () -> Unit,
    onOpenIdeas: () -> Unit,
    onToggleHabitToday: (Int) -> Unit,
    onToggleHabitForDate: (Int, LocalDate) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val pendingSuggestions = remember(timeSuggestions, dismissedSuggestions) {
        timeSuggestions.filter { (id, _) -> !dismissedSuggestions.contains(id) }.values.toList()
    }
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.habits_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onToggleDrawer,
                        enabled = isDrawerEnabled
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
                if (isLoading) {
                    DailyScreen(
                        habits = emptyList(),
                        isLoading = true,
                        onToggleHabitToday = {},
                        onHabitClick = {}
                    )
                } else if (habits.isEmpty()) {
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
                            .background(
                                if (androidx.compose.foundation.isSystemInDarkTheme())
                                    SurfaceLevel2
                                else GreyBlue
                            )
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
                                unselectedContentColor = SurfaceLevel2,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) Purple else Color.Transparent)
                                    .height(36.dp)
                            ) {
                                Text(
                                    text = stringResource(screen.titleRes),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }

                    Crossfade(
                        targetState = TabScreen.entries[selectedTabIndex],
                        label = "tab_fade",
                        modifier = Modifier.weight(1f)
                    ) { screen ->
                        when (screen) {
                            TabScreen.Daily -> DailyScreen(
                                habits = habits,
                                isLoading = false,
                                timeSuggestions = timeSuggestions,
                                onToggleHabitToday = { onToggleHabitToday(it.id) },
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
