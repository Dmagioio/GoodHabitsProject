package com.example.goodhabits.ui.screens.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.goodhabits.R
import com.example.goodhabits.viewmodel.HabitViewModel
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.ui.components.ColorCircle
import com.example.goodhabits.ui.components.DayChip
import com.example.goodhabits.ui.components.HabitTimePicker
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import com.example.goodhabits.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habit: Habit,
    onBack: () -> Unit,
    onSaveHabit: (Int, String, Color, Set<String>, Boolean, String) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    viewModel: HabitViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_habit_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
            onDeleteHabit = onDeleteHabit,
            viewModel = viewModel
        )
    }
}

@Composable
fun EditHabitContent(
    habit: Habit,
    modifier: Modifier = Modifier,
    onSaveHabit: (Int, String, Color, Set<String>, Boolean, String) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    viewModel: HabitViewModel
) {
    var title by remember(habit.id) { mutableStateOf(habit.title) }
    var motivation by remember(habit.id) { mutableStateOf(habit.motivation) }
    var reminderEnabled by remember(habit.id) { mutableStateOf(habit.reminderTime != null) }
    var deleteChecked by remember(habit.id) { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(habit.id) {
        habit.reminderTime?.let {
            viewModel.updateReminderTime(it.hour, it.minute)
        }
    }

    val pickedTime by viewModel.reminderTime.collectAsState()
    val context = LocalContext.current

    val habitColors = listOf(
        Purple,
        Yellow,
        LightBlue,
        Pink,
        Orange,
        LightGrey,
        Red,
    )
    var selectedColor by remember(habit.id) { mutableStateOf(Color(habit.colorHex.toInt())) }

    var selectedDays by remember { mutableStateOf(habit.days) }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.habit_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = stringResource(R.string.do_it_on))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val allDays = listOf(
                "SU" to stringResource(R.string.day_su),
                "MO" to stringResource(R.string.day_mo),
                "TU" to stringResource(R.string.day_tu),
                "WE" to stringResource(R.string.day_we),
                "TH" to stringResource(R.string.day_th),
                "FR" to stringResource(R.string.day_fr),
                "SA" to stringResource(R.string.day_sa)
            )
            allDays.forEach { (internalDay, displayDay) ->
                DayChip(
                    text = displayDay,
                    isSelected = selectedDays.contains(internalDay),
                    onClick = {
                        selectedDays = if (selectedDays.contains(internalDay)) {
                            selectedDays - internalDay
                        } else {
                            selectedDays + internalDay
                        }
                    }
                )
            }
        }

        OutlinedTextField(
            value = motivation,
            onValueChange = { motivation = it },
            label = { Text(stringResource(R.string.motivation_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.remind_me_label),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it }
            )
        }

        if (reminderEnabled) {
            Button(onClick = { showTimePicker = true }) {
                Text(stringResource(R.string.select_time, pickedTime))
            }
        }

        if (showTimePicker) {
            HabitTimePicker(
                initialTime = pickedTime,
                onTimeSelected = { time ->
                    viewModel.updateReminderTime(time.hour, time.minute)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }

        Text(text = stringResource(R.string.select_color))
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Checkbox(
                checked = deleteChecked,
                onCheckedChange = { deleteChecked = it },
                colors = CheckboxDefaults.colors(checkedColor = Red)
            )
            Text(
                text = stringResource(R.string.confirm_delete),
                style = MaterialTheme.typography.bodyMedium,
                color = if (deleteChecked) Red else Color.Unspecified
            )
        }

        Button(
            onClick = {
                if (deleteChecked) {
                    onDeleteHabit(habit.id)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = deleteChecked,
            colors = ButtonDefaults.buttonColors(containerColor = Red)
        ) {
            Text(stringResource(R.string.delete))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSaveHabit(habit.id, title, selectedColor, selectedDays, reminderEnabled, motivation)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentGradientStart, AccentGradientEnd)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                ),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text(stringResource(R.string.save), color = Color.White)
        }
    }
}

