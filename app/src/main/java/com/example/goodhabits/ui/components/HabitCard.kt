package com.example.goodhabits.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goodhabits.data.Habit
import androidx.compose.ui.graphics.toArgb

@Composable
fun HabitCard(
    habit: Habit,
    isCompletedToday: Boolean,
    onToggleHabit: () -> Unit,
    onClick: () -> Unit
) {
    val cardColor = Color(habit.colorHex.toInt())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
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
                    checkmarkColor = cardColor,
                    uncheckedColor = Color.White
                )
            )
        }
    }
}

