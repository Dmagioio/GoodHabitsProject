package com.example.goodhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.goodhabits.ui.theme.GoodHabitsTheme
import com.example.goodhabits.ui.theme.GreyBlue
import com.example.goodhabits.ui.theme.Purple
import kotlinx.coroutines.launch

enum class TabScreen(val title: String) {
    Daily("Щодня"),
    Weekly("Щотижня"),
    Overall("Загалом")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodHabitsTheme {
                val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text("Звички", modifier = Modifier.padding(16.dp))
                            NavigationDrawerItem(
                                label = { Text("Ідеї") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("Аналітика") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("Налаштування") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                }
                            )
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
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
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
                                            contentAlignment = androidx.compose.ui.Alignment.Center
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
                            Crossfade(targetState = selectedTabIndex, label = "TabContentCrossfade") { targetIndex ->
                                when (TabScreen.entries[targetIndex]) {
                                    TabScreen.Daily -> DailyScreen()
                                    TabScreen.Weekly -> WeeklyScreen()
                                    TabScreen.Overall -> OverallScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyScreen() {
    Text("Щоденний екран")
}

@Composable
fun WeeklyScreen() {
    Text("Щотижневий екран")
}

@Composable
fun OverallScreen() {
    Text("Загальний екран")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoodHabitsTheme {
        DailyScreen()
    }
}
