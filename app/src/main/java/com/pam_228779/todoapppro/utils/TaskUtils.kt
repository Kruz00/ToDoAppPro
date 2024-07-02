package com.pam_228779.todoapppro.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pam_228779.todoapppro.model.Task
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val TAG = "TaskUtils"

fun scheduleTaskReminder(context: Context, task: Task, reminderOffsetMinutes: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, TaskReminderReceiver::class.java).apply {
        putExtra("TASK_ID", task.id)
        putExtra("TASK_TITLE", task.title)
        putExtra("TASK_DESCRIPTION", task.description)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val reminderTime = Calendar.getInstance().apply {
        timeInMillis = task.dueAt.time - TimeUnit.MINUTES.toMillis(reminderOffsetMinutes)
    }

//    alarmManager.canScheduleExactAlarms()
    if (!alarmManager.canScheduleExactAlarms()) {
        val settingsIntent = Intent().apply {
            action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
        }
        context.startActivity(settingsIntent)
    }
    try {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime.timeInMillis, pendingIntent)
    } catch (e: SecurityException) {
        Log.e(TAG, "Unable to schedule exact alarm", e)
    }
}

fun cancelTaskReminder(context: Context, task: Task) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, TaskReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    alarmManager.cancel(pendingIntent)
}