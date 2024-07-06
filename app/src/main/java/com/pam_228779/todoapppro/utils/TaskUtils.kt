package com.pam_228779.todoapppro.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.pam_228779.todoapppro.model.Task
import java.time.Instant
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val TAG = "TaskUtils"

fun scheduleTaskReminder(context: Context, task: Task, reminderOffsetMinutes: Long) {
    val reminderTime = Calendar.getInstance().apply {
        timeInMillis = task.dueAt.time - TimeUnit.MINUTES.toMillis(reminderOffsetMinutes)
    }
    if (reminderTime.toInstant().isBefore(Instant.now())) {
        return
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, TaskReminderReceiver::class.java).apply {
        putExtra("TASK_ID", task.id)
        putExtra("TASK_TITLE", task.title)
        putExtra("TASK_DESCRIPTION", task.description)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        task.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    Log.i("scheduleTaskReminder", "Scheduling notification for task ${task.id} at ${reminderTime.time}, task due at: ${task.dueAt}")

//    alarmManager.canScheduleExactAlarms()
    if (!alarmManager.canScheduleExactAlarms()) {
        val settingsIntent = Intent().apply {
            action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(settingsIntent)
    }
    try {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime.timeInMillis, pendingIntent)
    } catch (e: SecurityException) {
        Log.e(TAG, "Unable to schedule exact alarm", e)
        Toast.makeText(context, "Unable to schedule exact alarm", Toast.LENGTH_SHORT).show()
    }
}

fun cancelTaskReminder(context: Context, task: Task) {
    val intent = Intent(context, TaskReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, task.id, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}