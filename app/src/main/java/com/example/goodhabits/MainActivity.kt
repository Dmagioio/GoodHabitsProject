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
import java.time.LocalDate
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
    AddHabit,
    EditHabit
}

data class Habit(
    val id: Int,
    val title: String,
    val color: Color,
    val completedDates: Set<Long> = emptySet()
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodHabitsTheme {
                var currentRootScreen by remember { mutableStateOf(RootScreen.Main) }
                var nextHabitId by remember { mutableIntStateOf(0) }
                val habits = remember { mutableStateListOf<Habit>() }
                var habitToEdit by remember { mutableStateOf<Habit?>(null) }

                // Функція перемикання виконання звички на певну дату
                val toggleHabitForDate: (Habit, LocalDate) -> Unit = { habit, date ->
                    val index = habits.indexOfFirst { it.id == habit.id }
                    if (index != -1) {
                        val current = habits[index]
                        val key = date.toEpochDay()
                        val updatedDates = current.completedDates.toMutableSet()
                        if (updatedDates.contains(key)) {
                            updatedDates.remove(key)
                        } else {
                            updatedDates.add(key)
                        }
                        habits[index] = current.copy(completedDates = updatedDates)
                    }
                }

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
                                                    onToggleHabitToday = { habit ->
                                                        toggleHabitForDate(
                                                            habit,
                                                            LocalDate.now()
                                                        )
                                                    },
                                                    onHabitClick = { habit ->
                                                        habitToEdit = habit
                                                        currentRootScreen = RootScreen.EditHabit
                                                    }
                                                )

                                                TabScreen.Weekly -> WeeklyScreen(
                                                    habits = habits,
                                                    onToggleHabitForDate = { habit, date ->
                                                        toggleHabitForDate(habit, date)
                                                    },
                                                    onHabitClick = { habit ->
                                                        habitToEdit = habit
                                                        currentRootScreen = RootScreen.EditHabit
                                                    }
                                                )

                                                TabScreen.Overall -> OverallScreen(
                                                    habits = habits,
                                                    onToggleHabitToday = { habit ->
                                                        toggleHabitForDate(
                                                            habit,
                                                            LocalDate.now()
                                                        )
                                                    },
                                                    onHabitClick = { habit ->
                                                        habitToEdit = habit
                                                        currentRootScreen = RootScreen.EditHabit
                                                    }
                                                )
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
                            onSaveHabit = { title, color ->
                                habits.add(
                                    Habit(
                                        id = nextHabitId++,
                                        title = title,
                                        color = color
                                    )
                                )
                                currentRootScreen = RootScreen.Main
                            }
                        )
                    }

                    RootScreen.EditHabit -> {
                        val habit = habitToEdit
                        if (habit == null) {
                            currentRootScreen = RootScreen.Main
                        } else {
                            EditHabitScreen(
                                habit = habit,
                                onBack = { currentRootScreen = RootScreen.Main },
                                onSaveHabit = { id, newTitle, newColor ->
                                    val index = habits.indexOfFirst { it.id == id }
                                    if (index != -1) {
                                        val existing = habits[index]
                                        habits[index] = existing.copy(
                                            title = newTitle,
                                            color = newColor
                                        )
                                        habitToEdit = habits[index]
                                    }
                                    currentRootScreen = RootScreen.Main
                                },
                                onDeleteHabit = { id ->
                                    val index = habits.indexOfFirst { it.id == id }
                                    if (index != -1) {
                                        habits.removeAt(index)
                                    }
                                    habitToEdit = null
                                    currentRootScreen = RootScreen.Main
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyScreen(
    habits: List<Habit>,
    onToggleHabitToday: (Habit) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
    val today = LocalDate.now().toEpochDay()

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
                    val isCompletedToday = habit.completedDates.contains(today)
                    HabitCard(
                        habit = habit,
                        isCompletedToday = isCompletedToday,
                        onToggleHabit = { onToggleHabitToday(habit) },
                        onClick = { onHabitClick(habit) }
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyScreen(
    habits: List<Habit>,
    onToggleHabitForDate: (Habit, LocalDate) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
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
                WeeklyHabitCard(
                    habit = habit,
                    onToggleForDate = { date ->
                        onToggleHabitForDate(habit, date)
                    },
                    onClick = { onHabitClick(habit) }
                )
            }
        }
    }
}

@Composable
fun OverallScreen(
    habits: List<Habit>,
    onToggleHabitToday: (Habit) -> Unit,
    onHabitClick: (Habit) -> Unit
) {
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
                OverallHabitCard(
                    habit = habit,
                    onToggleToday = { onToggleHabitToday(habit) },
                    onClick = { onHabitClick(habit) }
                )
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
    isCompletedToday: Boolean,
    onToggleHabit: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
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
                checked = isCompletedToday,
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
fun WeeklyHabitCard(
    habit: Habit,
    onToggleForDate: (LocalDate) -> Unit,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val dates = (6 downTo 0).map { today.minusDays(it.toLong()) } // 7 днів, від старшого до сьогодні

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
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

            // Дні тижня (підписи)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dayFormatter = SimpleDateFormat("E", Locale("uk"))
                dates.forEach { date ->
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, date.year)
                        set(Calendar.MONTH, date.monthValue - 1)
                        set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                    }
                    val dayName = dayFormatter.format(cal.time)
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            WeekDatesRow(
                dates = dates,
                habit = habit,
                onToggleForDate = onToggleForDate
            )
        }
    }
}

@Composable
fun WeekDatesRow(
    dates: List<LocalDate>,
    habit: Habit,
    onToggleForDate: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dates.forEach { date ->
            val key = date.toEpochDay()
            val isSelected = habit.completedDates.contains(key)
            DateCircle(
                number = date.dayOfMonth,
                selected = isSelected,
                color = habit.color
            ) {
                onToggleForDate(date)
            }
        }
    }
}

@Composable
fun DateCircle(
    number: Int,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
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
            )
            .clickable { onClick() },
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
fun OverallHabitCard(
    habit: Habit,
    onToggleToday: () -> Unit,
    onClick: () -> Unit
) {
    val todayKey = LocalDate.now().toEpochDay()
    val isCompletedToday = habit.completedDates.contains(todayKey)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
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
                    checked = isCompletedToday,
                    onCheckedChange = { onToggleToday() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = habit.color,
                        checkmarkColor = Color.White,
                        uncheckedColor = habit.color
                    )
                )
            }

            DotGrid(
                completedDates = habit.completedDates,
                activeColor = habit.color
            )
        }
    }
}

@Composable
fun DotGrid(
    rows: Int = 6,
    columns: Int = 18,
    completedDates: Set<Long>,
    activeColor: Color
) {
    val totalDots = rows * columns
    val today = LocalDate.now()
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(rows) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(columns) { column ->
                    val index = row * columns + column
                    // index = 0 -> найстаріший день, index = totalDots-1 -> сьогодні
                    val daysAgo = (totalDots - 1) - index
                    val date = today.minusDays(daysAgo.toLong())
                    val key = date.toEpochDay()
                    val isActive = completedDates.contains(key)
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
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onBack: () -> Unit,
    onSaveHabit: (String, Color) -> Unit
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habit: Habit,
    onBack: () -> Unit,
    onSaveHabit: (Int, String, Color) -> Unit,
    onDeleteHabit: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редагувати звичку") },
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
        EditHabitContent(
            habit = habit,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            onSaveHabit = onSaveHabit,
            onDeleteHabit = onDeleteHabit
        )
    }
}

@Composable
fun AddHabitContent(
    modifier: Modifier = Modifier,
    onSaveHabit: (String, Color) -> Unit
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
                    onSaveHabit(title, selectedColor)
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
fun EditHabitContent(
    habit: Habit,
    modifier: Modifier = Modifier,
    onSaveHabit: (Int, String, Color) -> Unit,
    onDeleteHabit: (Int) -> Unit
) {
    var title by remember(habit.id) { mutableStateOf(habit.title) }
    var motivation by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(false) }
    var deleteChecked by remember(habit.id) { mutableStateOf(false) }

    val habitColors = listOf(
        Purple,
        Color(0xFFFFC75F),
        Color(0xFF709CFF),
        Color(0xFFFF6F91),
        Color(0xFFFF9671),
        Color(0xFFB0BEC5)
    )
    var selectedColor by remember(habit.id) { mutableStateOf(habit.color) }

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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { deleteChecked = !deleteChecked },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Видалити звичку",
                color = Color(0xFFFF3B30),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Checkbox(
                checked = deleteChecked,
                onCheckedChange = { deleteChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFFF3B30),
                    checkmarkColor = Color.White,
                    uncheckedColor = Color(0xFFFF3B30)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (deleteChecked) {
                    onDeleteHabit(habit.id)
                } else if (title.isNotBlank()) {
                    onSaveHabit(habit.id, title, selectedColor)
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
                Habit(id = 0, title = "Пити воду", color = Purple),
                Habit(id = 1, title = "Читати книгу", color = Color(0xFFFF6F91))
            ),
            onToggleHabitToday = {},
            onHabitClick = {}
        )
    }
}
