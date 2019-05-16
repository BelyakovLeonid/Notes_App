package com.example.lab.noteapp.View

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.R
import com.example.lab.noteapp.Utils.MyAlarmReceiver
import com.example.lab.noteapp.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity: AppCompatActivity()  {
    private lateinit var noteViewModel: NoteViewModel
    private var noteId: Int = -1
    private var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.lab.noteapp.R.layout.activity_detail)
        setSupportActionBar(toolbar)

        noteId = intent.getIntExtra("noteId", -1)

        //получаем viewModel
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        noteViewModel.getNoteById(noteId).observe(this, Observer{
           it?.let {
               note = it
               refreshUI(it)
           }
        })
    }

    fun refreshUI(note: Note){
        note.alarm?.let {
            buttonAlarm.visibility = View.VISIBLE
            val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm")
            buttonAlarm.text = dateFormat.format(it)
        }

        titleView.text = note.title
        textView.text = note.text
        imageView.setImageURI(note.image?.toUri())
        layout.setBackgroundResource(note.colorRes)
    }

    fun deleteAlarm(id: Int){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(this, MyAlarmReceiver::class.java).apply{
            putExtra("noteId", id)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home ->{
                finish()
                true
            }
            R.id.action_edit ->{
                Intent(this, AddActivity::class.java).also {
                    it.putExtra("noteId", noteId)
                    startActivity(it)
                }
                finish()
                true
            }
            R.id.action_delete ->{
                note?.alarm?.let{
                    deleteAlarm(noteId)
                }
                noteViewModel.deleteById(noteId)
                val resIntent = Intent().apply {
                    putExtra("isDelete", true)
                }
                setResult(Activity.RESULT_CANCELED, resIntent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}