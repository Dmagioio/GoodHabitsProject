package com.example.goodhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.goodhabits.ui.theme.GoodHabitsTheme
import com.example.goodhabits.ui.theme.GreyBlue
import com.example.goodhabits.ui.theme.Purple
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class TabScreen(val title: String) {
    Daily("Щодня"),
    Weekly("Щотижня"),
    Overall("Загалом")
}

enum class RootScreen {
    Main,
    AddHabit
}

data class Habit(
    val title: String,
    val color: Color,
    val isCompleted: Boolean = false
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodHabitsTheme {
                var currentRootScreen by remember { mutableStateOf(RootScreen.Main) }
                val habits = remember { mutableStateListOf<Habit>() }

                when (currentRootScreen) {
                    RootScreen.Main -> {
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
                                        },
                                        actions = {
                                            IconButton(onClick = {
                                                currentRootScreen = RootScreen.AddHabit
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Filled.Add,
                                                    contentDescription = "Додати звичку"
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
                                            onCreateClick = {
                                                currentRootScreen = RootScreen.AddHabit
                                            },
                                            onIdeasClick = {
                                                // TODO: відкривати екран з ідеями
                                            }
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
                                                    onToggleHabit = { habit ->
                                                        val index = habits.indexOf(habit)
                                                        if (index != -1) {
                                                            val current = habits[index]
                                                            habits[index] =
                                                                current.copy(isCompleted = !current.isCompleted)
                                                        }
                                                    }
                                                )

                                                TabScreen.Weekly -> WeeklyScreen(habits = habits)
                                                TabScreen.Overall -> OverallScreen(habits = habits)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    RootScreen.AddHabit -> {
                        AddHabitScreen(
                            onBack = { currentRootScreen = RootScreen.Main },
                            onSaveHabit = { habit ->
                                habits.add(habit)
                                currentRootScreen = RootScreen.Main
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyScreen(
    habits: List<Habit>,
    onToggleHabit: (Habit) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        DateHeader()
        Spacer(modifier = Modifier.height(16.dp))

        if (habits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Натисніть +, щоб додати першу звичку",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(habits) { habit ->
                    HabitCard(habit = habit, onToggleHabit = { onToggleHabit(habit) })
                }
            }
        }
    }
}

@Composable
fun WeeklyScreen(habits: List<Habit>) {
    if (habits.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ще немає звичок для відстеження за тиждень",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(habits) { habit ->
                WeeklyHabitCard(habit = habit)
            }
        }
    }
}

@Composable
fun OverallScreen(habits: List<Habit>) {
    if (habits.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Додайте звичку, щоб побачити загальну статистику",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(habits) { habit ->
                OverallHabitCard(habit = habit)
            }
        }
    }
}

@Composable
fun EmptyHabitsContent(
    onCreateClick: () -> Unit,
    onIdeasClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "У вас ще немає звичок.",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple,
                contentColor = Color.White
            )
        ) {
            Text(text = "Створити нову звичку")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "або",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onIdeasClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3D7BFF),
                contentColor = Color.White
            )
        ) {
            Text(text = "Вибрати з ідей")
        }
    }
}

@Composable
fun DateHeader() {
    val calendar = remember { Calendar.getInstance() }
    val dayFormatter = remember { SimpleDateFormat("EEE", Locale("uk")) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }

    val dayName = dayFormatter.format(calendar.time)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val dateText = dateFormatter.format(calendar.time)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onToggleHabit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = habit.color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.title,
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Checkbox(
                checked = habit.isCompleted,
                onCheckedChange = { onToggleHabit() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    checkmarkColor = habit.color,
                    uncheckedColor = Color.White
                )
            )
        }
    }
}

@Composable
fun WeeklyHabitCard(habit: Habit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Дні тижня
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Вт", "Ср", "Чт", "Пт", "Сб", "Нд", "Пн").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            WeekDatesRow(color = habit.color)
        }
    }
}

@Composable
fun WeekDatesRow(color: Color) {
    val calendar = remember { Calendar.getInstance() }
    val today = calendar.get(Calendar.DAY_OF_MONTH)
    val dates = (today - 6..today).toList()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dates.forEachIndexed { index, date ->
            val isSelected = index == dates.lastIndex
            DateCircle(
                number = if (date > 0) date else 1,
                selected = isSelected,
                color = color
            )
        }
    }
}

@Composable
fun DateCircle(
    number: Int,
    selected: Boolean,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (selected) color else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (selected) color else Color.LightGray,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) Color.White else Color.Black
        )
    }
}

@Composable
fun OverallHabitCard(habit: Habit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Checkbox(
                    checked = habit.isCompleted,
                    onCheckedChange = { },
                    colors = CheckboxDefaults.colors(
                        checkedColor = habit.color,
                        checkmarkColor = Color.White,
                        uncheckedColor = habit.color
                    )
                )
            }

            DotGrid(activeColor = habit.color)
        }
    }
}

@Composable
fun DotGrid(
    rows: Int = 6,
    columns: Int = 18,
    activeDots: Int = 8,
    activeColor: Color
) {
    val totalDots = rows * columns
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(rows) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(columns) { column ->
                    val index = row * columns + column
                    val isActive = index >= totalDots - activeDots
                    Dot(
                        color = if (isActive) activeColor else Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onBack: () -> Unit,
    onSaveHabit: (Habit) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Нова звичка") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AddHabitContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            onSaveHabit = onSaveHabit
        )
    }
}

@Composable
fun AddHabitContent(
    modifier: Modifier = Modifier,
    onSaveHabit: (Habit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var motivation by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(false) }

    val habitColors = listOf(
        Purple,
        Color(0xFFFFC75F),
        Color(0xFF709CFF),
        Color(0xFFFF6F91),
        Color(0xFFFF9671),
        Color(0xFFB0BEC5)
    )
    var selectedColor by remember { mutableStateOf(habitColors.first()) }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Назва звички") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Я буду робити це в")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Нд", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб").forEach { day ->
                DayChip(text = day)
            }
        }

        OutlinedTextField(
            value = motivation,
            onValueChange = { motivation = it },
            label = { Text("Що мене мотивуватиме?") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Нагадати мені",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it }
            )
        }

        Text(
            text = "Встановити нагадування на 12:00",
            color = if (reminderEnabled) Color.Black else Color.Gray
        )

        Text(text = "Вибрати колір")
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            habitColors.forEach { color ->
                ColorCircle(
                    color = color,
                    selected = color == selectedColor,
                    onClick = { selectedColor = color }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSaveHabit(Habit(title = title, color = selectedColor))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple,
                contentColor = Color.White
            )
        ) {
            Text(text = "Зберегти")
        }
    }
}

@Composable
fun DayChip(text: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Purple)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun ColorCircle(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 3.dp else 0.dp,
                color = if (selected) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoodHabitsTheme {
        DailyScreen(
            habits = listOf(
                Habit(title = "Пити воду", color = Purple),
                Habit(title = "Читати книгу", color = Color(0xFFFF6F91))
            ),
            onToggleHabit = {}
        )
    }
}
