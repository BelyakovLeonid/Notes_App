package com.example.lab.noteapp.Utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {
    //имя файла, куда будем помещать информацию
    private val PREFS_FILENAME = "com.example.lab.test.prefs"

    //тэги, по готорому будем получать/размещать информацию
    private val LAST_ORIENTATION = "last_orientation"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,0)

    //ориентация, которая была в последний раз (true - линейная false - табличная)
    var lastOrientation: Boolean
        get() = prefs.getBoolean(LAST_ORIENTATION,true)
        set(value) = prefs.edit().putBoolean(LAST_ORIENTATION, value).apply()
}