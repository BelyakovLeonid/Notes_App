package com.example.lab.noteapp

import android.app.Application
import com.example.lab.noteapp.DataBase.NoteDB
import com.example.lab.noteapp.Utils.SharedPref

class App: Application() {

    companion object{
        val ACTION_RESCHEDULE_ALARMS = "ACTION_RESCHEDULE_ALARMS"
        val ACTION_SET_NOTIFICATION = "ACTION_SET_NOTIFICATION"
        val ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION"

        lateinit var instance: App
        lateinit var noteDB: NoteDB
        var prefs: SharedPref? = null
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        noteDB = NoteDB.getInstance(this)
        prefs = SharedPref(this)
    }
}