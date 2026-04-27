package com.example.goodhabits.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.goodhabits.domain.analysis.BehavioralAnalysisEngine
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.repository.ReminderScheduler
import com.example.goodhabits.domain.usecase.AddHabitUseCase
import com.example.goodhabits.domain.usecase.DeleteHabitUseCase
import com.example.goodhabits.domain.usecase.ToggleHabitForDateUseCase
import com.example.goodhabits.domain.usecase.UpdateHabitUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

internal class HabitActionHandler(
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val toggleHabitForDateUseCase: ToggleHabitForDateUseCase,
    private val draftStateHolder: HabitDraftStateHolder,
    private val screenStateHolder: HabitScreenStateHolder,
    private val analysisEngine: BehavioralAnalysisEngine,
    private val reminderScheduler: ReminderScheduler
) {
    suspend fun addHabit() {
        screenStateHolder.setLoading(true)
        try {
            val draft = draftStateHolder.snapshot()
            addHabitUseCase(
                Habit(
                    title = draft.title,
                    colorHex = draft.selectedColor.toArgb().toLong(),
                    days = draft.selectedDays,
                    reminderTime = if (draft.reminderEnabled) draft.reminderTime else null,
                    motivation = draft.motivation
                )
            )

            draftStateHolder.clear()
        } catch (e: Exception) {
            screenStateHolder.setError("Помилка додавання звички: ${e.localizedMessage}")
        } finally {
            screenStateHolder.setLoading(false)
        }
    }

    suspend fun updateHabit(
        id: Int,
        title: String,
        color: Color,
        days: Set<String>,
        isReminderEnabled: Boolean,
        motivation: String = "",
        customTime: java.time.LocalTime? = null
    ) {
        screenStateHolder.setLoading(true)
        try {
            val reminderTime = if (isReminderEnabled) {
                customTime ?: draftStateHolder.reminderTime.value
            } else null

            updateHabitUseCase(
                Habit(
                    id = id,
                    title = title,
                    colorHex = color.toArgb().toLong(),
                    days = days,
                    reminderTime = reminderTime,
                    motivation = motivation
                )
            )

            draftStateHolder.clear()
        } catch (e: Exception) {
            screenStateHolder.setError("Помилка оновлення звички: ${e.localizedMessage}")
        } finally {
            screenStateHolder.setLoading(false)
        }
    }

    suspend fun deleteHabit(id: Int) {
        screenStateHolder.setLoading(true)
        try {
            deleteHabitUseCase(id)
            draftStateHolder.clear()
        } catch (e: Exception) {
            screenStateHolder.setError("Помилка видалення звички: ${e.localizedMessage}")
        } finally {
            screenStateHolder.setLoading(false)
        }
    }

    suspend fun toggleHabitToday(habitId: Int) {
        try {
            toggleHabitForDateUseCase(habitId, LocalDate.now())
            
            checkHabitStacking(habitId)
        } catch (e: Exception) {
            screenStateHolder.setError("Сталася помилка: ${e.localizedMessage}")
        }
    }

    private fun checkHabitStacking(completedHabitId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val chains = analysisEngine.findHabitChains()
            val chain = chains.find { it.firstHabitId == completedHabitId }
            
            if (chain != null) {
                val nextHabit = screenStateHolder.uiState.value.habits.find { it.id == chain.secondHabitId }
                val today = LocalDate.now().toEpochDay()
                
                if (nextHabit != null &&
                    !nextHabit.completedDates.contains(today) &&
                    !screenStateHolder.uiState.value.sentStackingRemindersToday.contains(nextHabit.id)
                ) {
                    val scheduledTime = LocalTime.now().plusMinutes(10)
                    reminderScheduler.schedule(
                        habitId = nextHabit.id + 10000,
                        habitTitle = "Ти щойно закінчив ${screenStateHolder.uiState.value.habits.find { it.id == completedHabitId }?.title} — чудовий момент для ${nextHabit.title}!",
                        time = scheduledTime,
                        motivation = nextHabit.motivation
                    )
                    screenStateHolder.markStackingReminderSent(nextHabit.id)
                }
            }
        }
    }

    suspend fun toggleHabitForDate(habitId: Int, date: LocalDate) {
        try {
            toggleHabitForDateUseCase(habitId, date)
            if (date == LocalDate.now()) {
                checkHabitStacking(habitId)
            }
        } catch (e: Exception) {
            screenStateHolder.setError("Сталася помилка: ${e.localizedMessage}")
        }
    }
}
