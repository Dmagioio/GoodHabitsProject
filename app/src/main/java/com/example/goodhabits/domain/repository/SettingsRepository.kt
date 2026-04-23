package com.example.goodhabits.domain.repository

import kotlinx.coroutines.flow.Flow

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class AppLanguage {
    UK, EN
}

interface SettingsRepository {
    val theme: Flow<AppTheme>
    val language: Flow<AppLanguage>
    val globalNotificationsEnabled: Flow<Boolean>

    suspend fun setTheme(theme: AppTheme)
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setGlobalNotificationsEnabled(enabled: Boolean)
}
