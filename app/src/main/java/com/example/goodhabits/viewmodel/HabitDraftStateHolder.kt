package com.example.goodhabits.viewmodel

import androidx.compose.ui.graphics.Color
import com.example.goodhabits.ui.theme.Purple
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime

internal data class HabitDraftSnapshot(
    val title: String,
    val motivation: String,
    val selectedDays: Set<String>,
    val reminderEnabled: Boolean,
    val selectedColor: Color,
    val reminderTime: LocalTime
)

internal class HabitDraftStateHolder {
    private val _draftTitle = MutableStateFlow("")
    val draftTitle: StateFlow<String> = _draftTitle.asStateFlow()

    private val _draftMotivation = MutableStateFlow("")
    val draftMotivation: StateFlow<String> = _draftMotivation.asStateFlow()

    private val _draftSelectedDays = MutableStateFlow(setOf<String>())
    val draftSelectedDays: StateFlow<Set<String>> = _draftSelectedDays.asStateFlow()

    private val _isReminderEnabled = MutableStateFlow(false)
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    private val _draftSelectedColor = MutableStateFlow(Purple)
    val draftSelectedColor: StateFlow<Color> = _draftSelectedColor.asStateFlow()

    private val _reminderTime = MutableStateFlow(LocalTime.of(12, 0))
    val reminderTime: StateFlow<LocalTime> = _reminderTime.asStateFlow()

    fun updateDraftTitle(title: String) {
        _draftTitle.value = title
    }

    fun updateDraftMotivation(motivation: String) {
        _draftMotivation.value = motivation
    }

    fun updateDraftDays(days: Set<String>) {
        _draftSelectedDays.value = days
    }

    fun toggleReminder(isEnabled: Boolean) {
        _isReminderEnabled.value = isEnabled
    }

    fun updateDraftColor(color: Color) {
        _draftSelectedColor.value = color
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _reminderTime.value = LocalTime.of(hour, minute)
    }

    fun snapshot(): HabitDraftSnapshot = HabitDraftSnapshot(
        title = _draftTitle.value,
        motivation = _draftMotivation.value,
        selectedDays = _draftSelectedDays.value,
        reminderEnabled = _isReminderEnabled.value,
        selectedColor = _draftSelectedColor.value,
        reminderTime = _reminderTime.value
    )

    fun clear() {
        _draftTitle.value = ""
        _draftMotivation.value = ""
        _draftSelectedDays.value = emptySet()
        _isReminderEnabled.value = false
        _draftSelectedColor.value = Purple
    }
}
