package com.example.goodhabits.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.goodhabits.domain.repository.ReminderScheduler
import javax.inject.Inject
import javax.inject.Singleton
import java.time.LocalTime
import java.util.Calendar

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(habitId: Int, habitTitle: String, time: LocalTime) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("HABIT_TITLE", habitTitle)
            putExtra("HABIT_ID", habitId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        fun setTheAlarm() {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("HABIT_TITLE", habitTitle)
                putExtra("HABIT_ID", habitId)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context, habitId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmInfo, pendingIntent)

            Log.d("ALARM_DEBUG", "Будильник встановлено через setAlarmClock на ${calendar.time}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                setTheAlarm()
            } else {
                val intentSettings = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intentSettings)

                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            setTheAlarm()
        }
    }

    override fun cancel(habitId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
