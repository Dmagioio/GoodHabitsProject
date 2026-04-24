package com.example.goodhabits

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goodhabits.domain.repository.AppLanguage
import com.example.goodhabits.ui.navigation.HabitApp
import com.example.goodhabits.ui.theme.GoodHabitsTheme
import com.example.goodhabits.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val theme by settingsViewModel.theme.collectAsState()
            val language by settingsViewModel.language.collectAsState()

            val context = LocalContext.current
            LaunchedEffect(language) {
                val locale = if (language == AppLanguage.EN) {
                    Locale.forLanguageTag("en")
                } else {
                    Locale.forLanguageTag("uk")
                }
                Locale.setDefault(locale)
                val config = context.resources.configuration
                config.setLocale(locale)
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }

            GoodHabitsTheme(appTheme = theme) {
                HabitApp()
            }
        }
    }
}
