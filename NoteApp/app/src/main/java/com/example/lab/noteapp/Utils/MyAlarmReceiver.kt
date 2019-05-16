package com.example.lab.noteapp.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import com.example.lab.noteapp.App.Companion.ACTION_RESCHEDULE_ALARMS

class MyAlarmReceiver: BroadcastReceiver() {

    val CHANNEL_ID = "CHANNEL_ALARMS"

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {

            ACTION_BOOT_COMPLETED -> {
                //при перезагрузке устройства необходимо перезапустить все уведомления
                //т.к. при выключении каждое из них отменяется (.cancel())
                val serviceIntent = Intent(context, MyAlarmService::class.java)
                serviceIntent.action = ACTION_RESCHEDULE_ALARMS
                context.startService(serviceIntent)
            }

            else -> {
                val noteId = intent.getIntExtra("noteId", 1)

                Intent(context, MyAlarmService::class.java).apply {
                    putExtra("noteId", noteId)
                    context.startService(this)
                }
            }

        }
    }
}