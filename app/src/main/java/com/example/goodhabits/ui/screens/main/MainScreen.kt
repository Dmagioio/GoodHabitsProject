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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.goodhabits.data.Habit
import com.example.goodhabits.ui.components.EmptyHabitsContent
import com.example.goodhabits.ui.theme.GreyBlue
import com.example.goodhabits.ui.theme.Purple
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class TabScreen(val title: String) {
    Daily("Щодня"),
    Weekly("Щотижня"),
    Overall("Загалом")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    habits: List<Habit>,
    onOpenAddHabit: () -> Unit,
    onToggleHabitToday: (Int) -> Unit,
    onToggleHabitForDate: (Int, LocalDate) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF7F5FF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Звички",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(12.dp))

                    NavigationDrawerItem(
                        label = { Text("Ідеї") },
                        icon = { Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color.White,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Purple,
                            selectedIconColor = Purple
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text("Аналітика") },
                        icon = { Icon(imageVector = Icons.Filled.ShowChart, contentDescription = null) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color.White,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Purple,
                            selectedIconColor = Purple
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text("Налаштування") },
                        icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = null) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color.White,
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Purple,
                            selectedIconColor = Purple
                        )
                    )
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Звички") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
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
                        onIdeasClick = { /* TODO */ }
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
                                        text = screen.title,
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

