package com.pam_228779.todoapppro.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pam_228779.todoapppro.R
import java.util.concurrent.TimeUnit

class TaskReminderWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    private val TAG: String = "TaskReminderWorker"

    override fun doWork(): Result {
        val taskTitle = inputData.getString("TASK_TITLE") ?: "Task Reminder"
        val taskDueAt = inputData.getLong("TASK_DUE_AT", System.currentTimeMillis())

        if (System.currentTimeMillis() >= taskDueAt) {
            sendNotification(taskTitle)
        }

        return Result.success()
    }

    private fun sendNotification(taskTitle: String) {
        val builder = NotificationCompat.Builder(applicationContext, "TASK_CHANNEL")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Task Reminder")
            .setContentText("Don't forget to complete: $taskTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

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
                return
            }
            notify(1, builder.build())
        }
    }

//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                Log.e(TAG, "PERMISSION_GRANTED")
//                // FCM SDK (and your app) can post notifications.
//            } else {
//                Log.e(TAG, "NO_PERMISSION")
//                // Directly ask for the permission
////                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
}