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

import kotlinx.coroutines.SupervisorJob

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val habitTitle = intent.getStringExtra("HABIT_TITLE") ?: context.getString(R.string.notification_default_text)
        val habitMotivation = intent.getStringExtra("HABIT_MOTIVATION") ?: ""
        val habitId = intent.getIntExtra("HABIT_ID", -1)
        Log.d("ALARM_RECEIVER", "СИГНАЛ Є! $habitTitle")

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (habitId != -1) {
                    val now = LocalTime.now()
                    val scheduledHour = intent.getIntExtra("SCHEDULED_HOUR", now.hour)
                    val scheduledMinute = intent.getIntExtra("SCHEDULED_MINUTE", now.minute)
                    reminderScheduler.schedule(habitId, habitTitle, LocalTime.of(scheduledHour, scheduledMinute), habitMotivation)
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

                val contentTitle = context.getString(R.string.notification_content_title, habitTitle)
                val contentText = if (habitMotivation.isNotEmpty()) {
                    context.getString(R.string.notification_motivation_prefix, habitMotivation)
                } else {
                    context.getString(R.string.notification_no_motivation)
                }

                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                    .build()

                val notificationId = habitId
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(notificationId, notification)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}