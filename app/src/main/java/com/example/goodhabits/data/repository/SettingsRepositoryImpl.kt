package com.example.goodhabits.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.goodhabits.domain.repository.AppLanguage
import com.example.goodhabits.domain.repository.AppTheme
import com.example.goodhabits.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")
        val GLOBAL_NOTIFICATIONS = booleanPreferencesKey("global_notifications")
    }

    override val theme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val themeName = preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
        AppTheme.valueOf(themeName)
    }

    override val language: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        val langName = preferences[PreferencesKeys.LANGUAGE] ?: AppLanguage.UK.name
        AppLanguage.valueOf(langName)
    }

    override val globalNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GLOBAL_NOTIFICATIONS] ?: true
    }

    override suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }

    override suspend fun setGlobalNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GLOBAL_NOTIFICATIONS] = enabled
        }
    }
}
