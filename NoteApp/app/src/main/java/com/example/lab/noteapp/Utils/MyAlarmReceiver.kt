package com.example.lab.noteapp.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.example.lab.noteapp.R
import com.example.lab.noteapp.View.DetailActivity

class MyAlarmReceiver: BroadcastReceiver() {

    val CHANNEL_ID = "MY_CHANNEL"

    override fun onReceive(context: Context, intent: Intent) {

        createNotificationChannel(context)
        val noteId = intent.getIntExtra("noteId", 1)

        //создаем интент, который вызовется при нажатии на уведомление
        val notificationIntent = Intent(context, DetailActivity::class.java)
        notificationIntent.putExtra("noteId", noteId)

        //создаем "оболочку" для нашего интента
        val pendingIntent = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        //создаем уведомление
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("MyFirstNotification")
            .setContentText("ReallyFirst")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1, notification)
    }


    private fun createNotificationChannel(context: Context) {
        // Создание NotificationChannel требуется только для API 26+
        // потому что класс NotificationChannel новый и находится не в support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NAME"
            val descriptionText = "DESCRIPTION"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Регистрируем канал в системе
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}