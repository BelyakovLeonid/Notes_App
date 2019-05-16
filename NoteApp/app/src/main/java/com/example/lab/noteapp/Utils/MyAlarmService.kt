package com.example.lab.noteapp.Utils

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.example.lab.noteapp.App
import com.example.lab.noteapp.App.Companion.ACTION_RESCHEDULE_ALARMS
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.R
import com.example.lab.noteapp.View.DetailActivity
import java.util.*

class MyAlarmService: IntentService("Reschedule alarms") {

    val CHANNEL_ID = "CHANNEL_ALARMS"

    override fun onHandleIntent(intent: Intent) {
        createNotificationChannel(applicationContext)
        val noteId = intent.getIntExtra("noteId", 1)
        val dao = App.noteDB.getNoteDao()

        try{
            when(intent.action){
                ACTION_RESCHEDULE_ALARMS -> {
                    val notes = dao.getAllSync()
                    rescheduleAlarms(notes)
                }
                else ->{
                    val note = dao.getNoteSyinc(noteId)
                    setNotification(note)
                }
            }
        }catch (e: Exception){
            //Restore interrupt status
            Thread.currentThread().interrupt()
        }
    }

    private fun rescheduleAlarms(notes: Array<Note>){
        for (n in notes){
            if(n.alarm != null && n.alarm!! > Date().time){
                setNotification(n)
            }
        }
    }

    private fun setNotification(note: Note){
        val noteId = note.id
        val title = note.title
        val text = note.text
        val alarm = note.alarm

        //создаем интент, который вызовется при нажатии на уведомление
        val notificationIntent = Intent(this, DetailActivity::class.java)
        notificationIntent.putExtra("noteId", noteId)

        //создаем "оболочку" для нашего интента
        val pendingIntent = TaskStackBuilder.create(this).run{
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(noteId!!, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        //создаем уведомление
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(noteId!!, notification)
    }


    private fun deleteNotification(noteId: Int){
        Log.d("MyTag", "WE ARE IN DELETE")
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