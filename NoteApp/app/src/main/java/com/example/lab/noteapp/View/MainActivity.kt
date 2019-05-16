package com.example.lab.noteapp.View

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab.noteapp.App
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.R
import com.example.lab.noteapp.Utils.MyAlarmReceiver
import com.example.lab.noteapp.View.Adapter.NoteAdapter
import com.example.lab.noteapp.View.Adapter.NoteGridDecorator
import com.example.lab.noteapp.View.Adapter.NoteLinearDecorator
import com.example.lab.noteapp.ViewModel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*



class MainActivity : AppCompatActivity() {
    private lateinit var linearDecorator: NoteLinearDecorator
    private lateinit var gridDecorator: NoteGridDecorator
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private var orientation = true
    private var notes: Array<Note>? = null
    private lateinit var menu: Menu

    private val EDIT_NOTE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        orientation = App.prefs!!.lastOrientation

        setContentView(com.example.lab.noteapp.R.layout.activity_main)
        setSupportActionBar(toolbar)
        linearDecorator = NoteLinearDecorator(resources.getDimension(R.dimen.recycler_margin).toInt())
        gridDecorator = NoteGridDecorator(resources.getDimension(R.dimen.recycler_margin).toInt())

        //получаем viewModel
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        noteViewModel.getAllNotes().observe(this, Observer {
            notes = it
            refreshUI(it)
        })

        fab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        menu.findItem(R.id.action_search).collapseActionView()
    }

    fun onNoteClick(note: Note) {
        Intent(this, DetailActivity::class.java).also {
            it.putExtra("noteId", note.id)
            startActivityForResult(it, EDIT_NOTE_REQUEST)
        }
    }

    fun changeOrientation() {

        orientation = !orientation

        val item: MenuItem = menu.findItem(R.id.action_orientation)

        if (orientation) {
            resView.removeItemDecoration(gridDecorator)
            resView.addItemDecoration(linearDecorator)
            resView.layoutManager = LinearLayoutManager(this)
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_list)
        } else {
            resView.removeItemDecoration(linearDecorator)
            resView.addItemDecoration(gridDecorator)
            resView.layoutManager = GridLayoutManager(this, 2)
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_grid)
        }

        App.prefs!!.lastOrientation = orientation
    }

    fun refreshUI(notes: Array<Note>) {
        if (orientation) {
            resView.removeItemDecoration(linearDecorator)
            resView.addItemDecoration(linearDecorator)
            resView.layoutManager = LinearLayoutManager(this)
        } else {
            resView.removeItemDecoration(gridDecorator)
            resView.addItemDecoration(gridDecorator)
            resView.layoutManager = GridLayoutManager(this, 2)
        }
        noteAdapter = NoteAdapter(notes, this, { note: Note -> onNoteClick(note) })
        resView.adapter = noteAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu

        menu.findItem(R.id.action_orientation).icon =
            if (orientation)
                ContextCompat.getDrawable(this, R.drawable.ic_list)
            else
                ContextCompat.getDrawable(this, R.drawable.ic_grid)


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val itemSearch = menu.findItem(R.id.action_search).actionView as SearchView

        itemSearch.setSearchableInfo(
            searchManager.getSearchableInfo(
                ComponentName(applicationContext, SearchActivity::class.java)
            )
        )
        itemSearch.setIconifiedByDefault(false)

        menu.findItem(R.id.action_search).setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    menu.findItem(R.id.action_orientation).isVisible = false
                    menu.findItem(R.id.action_settings).isVisible = false
                    menu.findItem(R.id.action_deleteAll).isVisible = false
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    menu.findItem(R.id.action_orientation).isVisible = true
                    menu.findItem(R.id.action_settings).isVisible = true
                    menu.findItem(R.id.action_deleteAll).isVisible = true
                    return true
                }
            }
        )

        return true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_orientation -> {
                changeOrientation()
                true
            }
            R.id.action_deleteAll -> {
                for(n in notes!!){
                    if(n.alarm != null) deleteAlarm(n.id!!)
                }
                noteViewModel.deleteAll()
                Snackbar.make(coordinator, "Все заметки удалены", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            val isDelete = data?.extras?.getBoolean("isDelete")
            if (isDelete == true)
                Snackbar.make(coordinator, "Заметка удалена", Snackbar.LENGTH_SHORT).show()
        }
    }
}
