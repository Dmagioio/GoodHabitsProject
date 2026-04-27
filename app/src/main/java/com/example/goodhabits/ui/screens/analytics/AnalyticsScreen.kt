package com.example.goodhabits.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goodhabits.R
import com.example.goodhabits.viewmodel.AnalyticsPeriod
import com.example.goodhabits.viewmodel.AnalyticsViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val accentColor = remember(uiState.selectedHabitId, uiState.habits) {
        val selectedHabit = uiState.habits.find { it.id == uiState.selectedHabitId }
        if (selectedHabit != null) Color(selectedHabit.colorHex) else Color(0xFF4DB6AC)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.analytics), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedHabitId == null,
                        onClick = { viewModel.selectHabit(null) },
                        label = { Text(stringResource(R.string.all_habits)) },
                        enabled = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White,
                            selectedLabelColor = Color.Black
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.LightGray,
                            selectedBorderColor = Color.Transparent,
                            borderWidth = 1.dp,
                            enabled = true,
                            selected = false,
                        )
                    )
                }
                items(uiState.habits) { habit ->
                    FilterChip(
                        selected = uiState.selectedHabitId == habit.id,
                        onClick = { viewModel.selectHabit(habit.id) },
                        label = { Text(habit.title) },
                        enabled = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.completed_habits_section),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF0F0F0),
                    onClick = { viewModel.togglePeriod() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.period == AnalyticsPeriod.Weekly) 
                                stringResource(R.string.weekly_label) 
                            else stringResource(R.string.monthly_label),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "${uiState.allCompletedDates.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.total_completed_habits),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.period == AnalyticsPeriod.Weekly) {
                        WeeklyBarChart(viewModel.getCompletionsPerDayOfWeek(), accentColor)
                    } else {
                        MonthlyBarChart(viewModel.getCompletionsPerDayOfMonth(), uiState.currentMonth, accentColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.habit_heatmap_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.previousMonth() }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = accentColor)
                        }
                        Text(
                            text = "${uiState.currentMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("uk"))} ${uiState.currentMonth.year}",
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = accentColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        val daysOfWeek = listOf("Нд", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
                        daysOfWeek.forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val firstDayOfMonth = uiState.currentMonth.atDay(1)
                    val daysInMonth = uiState.currentMonth.lengthOfMonth()
                    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday

                    var currentDay = 1
                    for (row in 0..5) {
                        if (currentDay > daysInMonth) break
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (col in 0..6) {
                                val dayIndex = row * 7 + col
                                if (dayIndex < firstDayOfWeek || currentDay > daysInMonth) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else {
                                    val date = uiState.currentMonth.atDay(currentDay)
                                    val isCompleted = uiState.allCompletedDates.contains(date.toEpochDay())
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(accentColor)
                                            )
                                        }
                                        Text(
                                            text = "$currentDay",
                                            color = if (isCompleted) Color.White else if (date == LocalDate.now()) accentColor else Color.Black,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    currentDay++
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun WeeklyBarChart(completionsPerDay: Map<DayOfWeek, Int>, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд")
        val maxCompletions = completionsPerDay.values.maxOrNull() ?: 1

        days.forEachIndexed { index, day ->
            val dayOfWeek = when(index) {
                0 -> DayOfWeek.MONDAY
                1 -> DayOfWeek.TUESDAY
                2 -> DayOfWeek.WEDNESDAY
                3 -> DayOfWeek.THURSDAY
                4 -> DayOfWeek.FRIDAY
                5 -> DayOfWeek.SATURDAY
                else -> DayOfWeek.SUNDAY
            }
            val count = completionsPerDay[dayOfWeek] ?: 0
            val progress = count.toFloat() / maxCompletions.coerceAtLeast(1)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(progress)
                            .clip(RoundedCornerShape(15.dp))
                            .background(if (count > 0) accentColor else Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(day, style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Column(
            modifier = Modifier.height(150.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$maxCompletions", style = MaterialTheme.typography.bodySmall)
            Text("0", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun MonthlyBarChart(completionsPerDay: Map<Int, Int>, currentMonth: YearMonth, accentColor: Color) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val maxCompletions = completionsPerDay.values.maxOrNull() ?: 1

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            for (day in 1..daysInMonth) {
                val count = completionsPerDay[day] ?: 0
                val progress = count.toFloat() / maxCompletions.coerceAtLeast(1)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(progress.coerceAtLeast(0.05f))
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                        .background(if (count > 0) accentColor else Color(0xFFF5F5F5))
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1", style = MaterialTheme.typography.bodySmall)
            Text("${daysInMonth / 3}", style = MaterialTheme.typography.bodySmall)
            Text("${(daysInMonth * 2) / 3}", style = MaterialTheme.typography.bodySmall)
            Text("$daysInMonth", style = MaterialTheme.typography.bodySmall)
        }
    }
}
