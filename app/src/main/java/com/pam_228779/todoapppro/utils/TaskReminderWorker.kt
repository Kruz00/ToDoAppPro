package com.pam_228779.todoapppro.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.view.activity.TaskDetailActivity


class TaskReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
): Worker(appContext, workerParams) {

    private val TAG: String = "TaskReminderWorker"

    override fun doWork(): Result {
        val taskId = inputData.getInt("TASK_ID", -1)
        val taskTitle = inputData.getString("TASK_TITLE") ?: "Task"
        val taskDescription = inputData.getString("TASK_DESCRIPTION") ?: "You have a task to complete."

        createNotificationChannel()

        // Create an intent to open the task details
        val intent = Intent(applicationContext, TaskDetailActivity::class.java).apply {
            putExtra("TASK_ID", taskId)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext,
            taskId, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, "TASK_REMINDER_CHANNEL")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return Result.failure()
            }
            notify(taskId, notification)
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        val name = "Task Reminder"
        val descriptionText = "Channel for task reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("TASK_REMINDER_CHANNEL", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}