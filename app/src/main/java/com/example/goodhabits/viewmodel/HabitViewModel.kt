package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.example.goodhabits.domain.analysis.BehavioralAnalysisEngine
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.repository.ReminderScheduler
import com.example.goodhabits.domain.usecase.AddHabitUseCase
import com.example.goodhabits.domain.usecase.DeleteHabitUseCase
import com.example.goodhabits.domain.usecase.ObserveHabitsUseCase
import com.example.goodhabits.domain.usecase.ToggleHabitForDateUseCase
import com.example.goodhabits.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.String

@HiltViewModel
class HabitViewModel @Inject constructor(
    observeHabitsUseCase: ObserveHabitsUseCase,
    addHabitUseCase: AddHabitUseCase,
    updateHabitUseCase: UpdateHabitUseCase,
    deleteHabitUseCase: DeleteHabitUseCase,
    toggleHabitForDateUseCase: ToggleHabitForDateUseCase,
    private val analysisEngine: BehavioralAnalysisEngine,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {
    private val draftStateHolder = HabitDraftStateHolder()
    private val screenStateHolder = HabitScreenStateHolder(analysisEngine)
    private val actionHandler = HabitActionHandler(
        addHabitUseCase = addHabitUseCase,
        updateHabitUseCase = updateHabitUseCase,
        deleteHabitUseCase = deleteHabitUseCase,
        toggleHabitForDateUseCase = toggleHabitForDateUseCase,
        draftStateHolder = draftStateHolder,
        screenStateHolder = screenStateHolder,
        analysisEngine = analysisEngine,
        reminderScheduler = reminderScheduler,
        scope = viewModelScope
    )

    val draftTitle: StateFlow<String> = draftStateHolder.draftTitle
    val draftMotivation: StateFlow<String> = draftStateHolder.draftMotivation
    val draftSelectedDays: StateFlow<Set<String>> = draftStateHolder.draftSelectedDays
    val isReminderEnabled: StateFlow<Boolean> = draftStateHolder.isReminderEnabled
    val draftSelectedColor: StateFlow<Color> = draftStateHolder.draftSelectedColor
    val reminderTime: StateFlow<LocalTime> = draftStateHolder.reminderTime
    val uiState: StateFlow<HabitUiState> = screenStateHolder.uiState

    init {
        screenStateHolder.setLoading(true)
        viewModelScope.launch {
            observeHabitsUseCase().collect { list ->
                screenStateHolder.updateHabits(list)
            }
        }
    }

    fun updateDraftTitle(title: String) = draftStateHolder.updateDraftTitle(title)
    fun updateDraftMotivation(motivation: String) = draftStateHolder.updateDraftMotivation(motivation)
    fun updateDraftDays(days: Set<String>) = draftStateHolder.updateDraftDays(days)
    fun toggleReminder(isEnabled: Boolean) = draftStateHolder.toggleReminder(isEnabled)

    fun updateDraftColor(color: Color) = draftStateHolder.updateDraftColor(color)
    fun updateReminderTime(hour: Int, minute: Int) = draftStateHolder.updateReminderTime(hour, minute)

    fun clearDraft() = draftStateHolder.clear()

    fun addHabit() {
        viewModelScope.launch {
            actionHandler.addHabit()
        }
    }

    fun updateHabit(id: Int, title: String, color: Color, days: Set<String>, isReminderEnabled: Boolean, motivation: String = "", time: LocalTime? = null) {
        viewModelScope.launch {
            actionHandler.updateHabit(id, title, color, days, isReminderEnabled, motivation, time)
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            actionHandler.deleteHabit(id)
        }
    }

    fun toggleHabitToday(habitId: Int) {
        viewModelScope.launch {
            actionHandler.toggleHabitToday(habitId)
        }
    }

    fun toggleHabitForDate(habitId: Int, date: LocalDate) {
        viewModelScope.launch {
            actionHandler.toggleHabitForDate(habitId, date)
        }
    }

    fun clearError() {
        screenStateHolder.setError(null)
    }

    fun dismissSuggestion(habitId: Int) {
        screenStateHolder.dismissSuggestion(habitId)
    }
}
