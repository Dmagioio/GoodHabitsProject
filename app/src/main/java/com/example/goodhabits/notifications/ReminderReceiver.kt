package com.example.goodhabits.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.goodhabits.MainActivity
import com.example.goodhabits.R
import com.example.goodhabits.domain.repository.ReminderScheduler
import com.example.goodhabits.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val habitTitle = intent.getStringExtra("HABIT_TITLE") ?: context.getString(R.string.notification_default_text)
        val habitId = intent.getIntExtra("HABIT_ID", -1)
        Log.d("ALARM_RECEIVER", "СИГНАЛ Є! $habitTitle")

        CoroutineScope(Dispatchers.Main).launch {
            // Реплануємо будильник на завтра
            if (habitId != -1) {
                val now = LocalTime.now()
                // Використовуємо той самий час, що був встановлений
                // Для простоти візьмемо поточну годину/хвилину, але в реалі 
                // краще було б передавати початковий час через intent
                val scheduledHour = intent.getIntExtra("SCHEDULED_HOUR", now.hour)
                val scheduledMinute = intent.getIntExtra("SCHEDULED_MINUTE", now.minute)
                reminderScheduler.schedule(habitId, habitTitle, LocalTime.of(scheduledHour, scheduledMinute))
            }

            val notificationsEnabled = settingsRepository.globalNotificationsEnabled.first()
            if (!notificationsEnabled) {
                Log.d("ALARM_RECEIVER", "Сповіщення вимкнені глобально (DND).")
                return@launch
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "habit_reminder_channel"

            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(context.getString(R.string.notification_content_title))
                .setContentText(habitTitle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()

            val notificationId = System.currentTimeMillis().toInt()
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(notificationId, notification)
            }
        }
    }
}