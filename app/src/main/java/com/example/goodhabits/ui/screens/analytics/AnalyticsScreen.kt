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
        if (selectedHabit != null) Color(selectedHabit.colorHex) else Color(0xFF9C27B0)
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
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(uiState.habits) { habit ->
                    FilterChip(
                        selected = uiState.selectedHabitId == habit.id,
                        onClick = { viewModel.selectHabit(habit.id) },
                        label = { Text(habit.title) },
                        enabled = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(habit.colorHex),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.success_rate),
                    value = "${(uiState.successRate * 100).toInt()}%",
                    subtitle = formatTrend(uiState.trend),
                    accentColor = accentColor
                )
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.best_day),
                    value = uiState.bestDayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.getDefault()) ?: "-",
                    subtitle = stringResource(R.string.habit_heatmap_title),
                    accentColor = accentColor
                )
                InsightCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.total_progress),
                    value = "${uiState.totalCompletions}",
                    subtitle = stringResource(R.string.total_completed_habits),
                    accentColor = accentColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            uiState.insightDayRes?.let { dayResId ->
                val dayName = stringResource(dayResId)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = accentColor.copy(alpha = if (androidx.compose.foundation.isSystemInDarkTheme()) 0.25f else 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.insight_weakest_day, dayName),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (androidx.compose.foundation.isSystemInDarkTheme())
                                Color.White
                            else Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

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
                    color = MaterialTheme.colorScheme.surface,
                    onClick = { viewModel.togglePeriod() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.period == AnalyticsPeriod.Weekly) 
                                stringResource(R.string.weekly_label) 
                            else stringResource(R.string.monthly_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (uiState.period == AnalyticsPeriod.Weekly) {
                        WeeklySuccessChart(uiState.weeklyData, accentColor)
                    } else {
                        MonthlyTrendChart(uiState.monthlyTrendData, accentColor)
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                        Text(
                            text = "${uiState.currentMonth.month.getDisplayName(TextStyle.FULL, configuration.locales[0]).replaceFirstChar { if (it.isLowerCase()) it.titlecase(configuration.locales[0]) else it.toString() }} ${uiState.currentMonth.year}",
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = accentColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        val daysOfWeek = listOf(
                            stringResource(R.string.day_su),
                            stringResource(R.string.day_mo),
                            stringResource(R.string.day_tu),
                            stringResource(R.string.day_we),
                            stringResource(R.string.day_th),
                            stringResource(R.string.day_fr),
                            stringResource(R.string.day_sa)
                        )
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
                    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

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
                                    val successRate = uiState.heatmapData[date] ?: 0f
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (successRate > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(accentColor.copy(alpha = (successRate * 0.8f + 0.2f).coerceIn(0.2f, 1f)))
                                            )
                                        }
                                        Text(
                                            text = "$currentDay",
                                            color = if (successRate > 0.5f) Color.White 
                                                   else if (date == LocalDate.now()) accentColor 
                                                   else MaterialTheme.colorScheme.onSurface,
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
fun InsightCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (subtitle.contains("↑")) Color(0xFF4CAF50) else if (subtitle.contains("↓")) Color(0xFFF44336) else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun formatTrend(trend: Float): String {
    val percent = (trend * 100).toInt()
    return when {
        percent > 0 -> stringResource(R.string.trend_up, percent)
        percent < 0 -> stringResource(R.string.trend_down, -percent)
        else -> stringResource(R.string.trend_stable)
    }
}

@Composable
fun WeeklySuccessChart(data: List<Pair<LocalDate, Float>>, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (date, success) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(success.coerceAtLeast(0.05f))
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (success > 0) accentColor else Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MonthlyTrendChart(data: List<Pair<LocalDate, Float>>, accentColor: Color) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (_, success) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(success.coerceAtLeast(0.05f))
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                        .background(if (success > 0) accentColor else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.monthly_label), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(stringResource(R.string.today), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
