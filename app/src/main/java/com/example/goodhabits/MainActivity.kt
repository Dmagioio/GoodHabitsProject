package com.example.goodhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.goodhabits.ui.navigation.HabitApp
import com.example.goodhabits.ui.theme.GoodHabitsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodHabitsTheme {
                HabitApp()
            }
        }
    }
}