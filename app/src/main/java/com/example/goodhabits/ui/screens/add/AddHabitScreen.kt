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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        Yellow,
        LightBlue,
        Pink,
        Orang,
        LightGrey,
        Red,
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

