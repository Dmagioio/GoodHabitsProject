package com.example.goodhabits.ui.screens.edit

import android.app.TimePickerDialog
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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.goodhabits.viewmodel.HabitViewModel
import com.example.goodhabits.data.Habit
import com.example.goodhabits.ui.components.ColorCircle
import com.example.goodhabits.ui.components.DayChip
import com.example.goodhabits.ui.theme.LightBlue
import com.example.goodhabits.ui.theme.LightGrey
import com.example.goodhabits.ui.theme.Orang
import com.example.goodhabits.ui.theme.Pink
import com.example.goodhabits.ui.theme.Purple
import com.example.goodhabits.ui.theme.Red
import com.example.goodhabits.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habit: Habit,
    onBack: () -> Unit,
    onSaveHabit: (Int, String, Color, Set<String>, Boolean) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    viewModel: HabitViewModel
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
            onDeleteHabit = onDeleteHabit,
            viewModel = viewModel
        )
    }
}

@Composable
fun EditHabitContent(
    habit: Habit,
    modifier: Modifier = Modifier,
    onSaveHabit: (Int, String, Color, Set<String>, Boolean) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    viewModel: HabitViewModel
) {
    var title by remember(habit.id) { mutableStateOf(habit.title) }
    var motivation by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(false) }
    var deleteChecked by remember(habit.id) { mutableStateOf(false) }

    val pickedTime by viewModel.reminderTime.collectAsState()
    val context = LocalContext.current

    val habitColors = listOf(
        Purple,
        Yellow,
        LightBlue,
        Pink,
        Orang,
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
            label = { Text("Назва звички") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Я буду робити це в")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val allDays = listOf("Нд", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
            allDays.forEach { day ->
                DayChip(
                    text = day,
                    isSelected = selectedDays.contains(day),
                    onClick = {
                        selectedDays = if (selectedDays.contains(day)) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    }
                )
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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

        if (reminderEnabled) {
            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        viewModel.updateReminderTime(hour, minute)
                    },
                    pickedTime.hour,
                    pickedTime.minute,
                    true
                ).show()
            }) {
                Text("Вибрати час: $pickedTime")
            }
        }

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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "Видалити звичку",
                color = Red,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Checkbox(
                checked = deleteChecked,
                onCheckedChange = { deleteChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Red,
                    checkmarkColor = Color.White,
                    uncheckedColor = Red,
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (deleteChecked) {
                    onDeleteHabit(habit.id)
                } else if (title.isNotBlank()) {
                    onSaveHabit(habit.id, title, selectedColor, selectedDays, reminderEnabled)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple,
                contentColor = Color.White
            )
        ) {
            Text(text = "Зберегти")
        }
    }
}

