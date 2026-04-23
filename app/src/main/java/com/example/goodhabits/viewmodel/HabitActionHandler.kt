package com.example.goodhabits.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.goodhabits.domain.model.Habit
import com.example.goodhabits.domain.usecase.AddHabitUseCase
import com.example.goodhabits.domain.usecase.DeleteHabitUseCase
import com.example.goodhabits.domain.usecase.ToggleHabitForDateUseCase
import com.example.goodhabits.domain.usecase.UpdateHabitUseCase
import java.time.LocalDate

internal class HabitActionHandler(
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val toggleHabitForDateUseCase: ToggleHabitForDateUseCase,
    private val draftStateHolder: HabitDraftStateHolder,
    private val screenStateHolder: HabitScreenStateHolder
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
                    reminderTime = if (draft.reminderEnabled) draft.reminderTime else null
                )
            )

            draftStateHolder.clear()
        } catch (e: Exception) {
            screenStateHolder.setError("Помилка додавання звички: ${e.localizedMessage}")
        }
    }

    suspend fun updateHabit(
        id: Int,
        title: String,
        color: Color,
        days: Set<String>,
        isReminderEnabled: Boolean
    ) {
        screenStateHolder.setLoading(true)
        try {
            updateHabitUseCase(
                Habit(
                    id = id,
                    title = title,
                    colorHex = color.toArgb().toLong(),
                    days = days,
                    reminderTime = if (isReminderEnabled) draftStateHolder.reminderTime.value else null
                )
            )

            draftStateHolder.clear()
        } catch (e: Exception) {
            screenStateHolder.setError("Помилка оновлення звички: ${e.localizedMessage}")
        }
    }

    suspend fun deleteHabit(id: Int) {
        screenStateHolder.setLoading(true)
        try {
            deleteHabitUseCase(id)
            draftStateHolder.clear()
        } catch (e: Exception) {
            screenStateHolder.setError("Помилка видалення звички: ${e.localizedMessage}")
        }
    }

    suspend fun toggleHabitToday(habitId: Int) {
        try {
            toggleHabitForDateUseCase(habitId, LocalDate.now())
        } catch (e: Exception) {
            screenStateHolder.setError("Сталася помилка: ${e.localizedMessage}")
        }
    }

    suspend fun toggleHabitForDate(habitId: Int, date: LocalDate) {
        try {
            toggleHabitForDateUseCase(habitId, date)
        } catch (e: Exception) {
            screenStateHolder.setError("Сталася помилка: ${e.localizedMessage}")
        }
    }
}
