package com.example.goodhabits.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateHeader() {
    val today = remember { LocalDate.now() }
    val dayFormatter = remember(Locale.getDefault()) { DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()) }
    val dateFormatter = remember(Locale.getDefault()) { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.getDefault()) }

    val dayName = today.format(dayFormatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val dateText = today.format(dateFormatter)

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
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

