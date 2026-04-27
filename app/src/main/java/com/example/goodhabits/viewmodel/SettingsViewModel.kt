package com.example.goodhabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.domain.repository.AppLanguage
import com.example.goodhabits.domain.repository.AppTheme
import com.example.goodhabits.domain.repository.HabitRepository
import com.example.goodhabits.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val habitRepository: HabitRepository
) : ViewModel() {

    val theme: StateFlow<AppTheme> = settingsRepository.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM)

    val language: StateFlow<AppLanguage> = settingsRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.SYSTEM)

    val globalNotificationsEnabled: StateFlow<Boolean> = settingsRepository.globalNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }

    fun setGlobalNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setGlobalNotificationsEnabled(enabled)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            habitRepository.deleteAllHabits()
        }
    }
}
