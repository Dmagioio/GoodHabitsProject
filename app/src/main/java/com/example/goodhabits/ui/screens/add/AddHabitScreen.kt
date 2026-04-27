package com.example.goodhabits.ui.screens.add

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.goodhabits.ui.components.ColorCircle
import com.example.goodhabits.ui.components.DayChip
import com.example.goodhabits.ui.components.HabitTimePicker
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import com.example.goodhabits.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onBack: () -> Unit,
    viewModel: HabitViewModel,
    onSaveHabit: () -> Unit,
    preFilledTitle: String? = null
) {
    LaunchedEffect(Unit) {
        if (preFilledTitle != null && !preFilledTitle.contains("{title}")) {
            viewModel.updateDraftTitle(preFilledTitle)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_habit_title)) },
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
        AddHabitContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            viewModel = viewModel,
            onSaveHabit = onSaveHabit
        )
    }
}

@Composable
fun AddHabitContent(
    modifier: Modifier = Modifier,
    viewModel: HabitViewModel,
    onSaveHabit: () -> Unit
) {
    val title by viewModel.draftTitle.collectAsState()
    val motivation by viewModel.draftMotivation.collectAsState()
    val reminderEnabled by viewModel.isReminderEnabled.collectAsState()
    val pickedTime by viewModel.reminderTime.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }
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
    val selectedColor by viewModel.draftSelectedColor.collectAsState()

    val habitDays = listOf(
        "SU" to stringResource(R.string.day_su),
        "MO" to stringResource(R.string.day_mo),
        "TU" to stringResource(R.string.day_tu),
        "WE" to stringResource(R.string.day_we),
        "TH" to stringResource(R.string.day_th),
        "FR" to stringResource(R.string.day_fr),
        "SA" to stringResource(R.string.day_sa)
    )
    val selectedDays by viewModel.draftSelectedDays.collectAsState()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.updateDraftTitle(it) },
            label = { Text(stringResource(R.string.habit_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = stringResource(R.string.do_it_on))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            habitDays.forEach { (internalDay, displayDay) ->
                DayChip(
                    text = displayDay,
                    isSelected = selectedDays.contains(internalDay),
                    onClick = {
                        val newDays = if (selectedDays.contains(internalDay)) selectedDays - internalDay else selectedDays + internalDay
                        viewModel.updateDraftDays(newDays)
                    }
                )
            }
        }

        OutlinedTextField(
            value = motivation,
            onValueChange = { viewModel.updateDraftMotivation(it) },
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
                onCheckedChange = { viewModel.toggleReminder(it) }
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
                    onClick = { viewModel.updateDraftColor(color) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onSaveHabit()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentGradientStart, AccentGradientEnd)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Text(stringResource(R.string.save), color = Color.White)
        }
    }
}

