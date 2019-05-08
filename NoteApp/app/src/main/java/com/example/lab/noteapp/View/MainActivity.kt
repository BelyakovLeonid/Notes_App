package com.example.lab.noteapp.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab.noteapp.App
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.R
import com.example.lab.noteapp.View.Adapter.NoteAdapter
import com.example.lab.noteapp.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private var orientation = true
    private lateinit var menu: Menu


    private val NEW_NOTE_REQUEST = 1
    private val EDIT_NOTE_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        orientation = App.prefs!!.lastOrientation

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //получаем viewModel
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        noteViewModel.getAllNotes().observe(this, Observer{
            refreshUI(it)
        })



        fab.setOnClickListener {
            startActivityForResult(Intent(this, AddActivity::class.java),NEW_NOTE_REQUEST)
        }
    }

    fun onNoteClick(note: Note){
        Intent(this, DetailActivity::class.java).also {
            it.putExtra("noteId", note.id)
            startActivityForResult(it, EDIT_NOTE_REQUEST)
        }

    }

    fun changeOrientation(){

        orientation = !orientation

        val item: MenuItem =  menu.findItem(R.id.action_orientation)

        if(orientation){
            resView.layoutManager = LinearLayoutManager(this)
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_list)
        }else{
            resView.layoutManager = GridLayoutManager(this, 2)
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_grid)
        }

        App.prefs!!.lastOrientation = orientation
    }

    fun refreshUI(notes: Array<Note>) {
        if(orientation){
            resView.layoutManager = LinearLayoutManager(this)
        }else{
            resView.layoutManager = GridLayoutManager(this, 2)
        }
        noteAdapter = NoteAdapter(notes, this, {note: Note -> onNoteClick(note)})
        resView.adapter = noteAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu

        val item: MenuItem =  menu.findItem(R.id.action_orientation)

        if (orientation){
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_list)
        }else{
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_grid)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_orientation ->{
                changeOrientation()
                true
            }
            R.id.action_deleteAll ->{
                noteViewModel.deleteAll()
                Toast.makeText(this, "Все заметки удалены", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getNoteFromIntent(data: Intent?): Note {
        val title = data?.extras?.getString("title")
        val text = data?.extras?.getString("text")
        val image = data?.extras?.getString("image")
        val color = data?.extras?.getInt("color")

        val note = Note(null, title, text, image, null, color!!)

        return note
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == NEW_NOTE_REQUEST && resultCode == Activity.RESULT_OK){

            noteViewModel.addNote(getNoteFromIntent(data))
        }

        if(requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK){
            val id = data?.extras?.getInt("noteId")
            val title = data?.extras?.getString("title")
            val text = data?.extras?.getString("text")
            val image = data?.extras?.getString("image")
            val color = data?.extras?.getInt("color")

            val note = Note(id, title, text, image, null, color!!)

            noteViewModel.editNote(note)
        }

        if(requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_CANCELED){
            val isDelete = data?.extras?.getBoolean("isDelete")

            Log.d("MyTag","$isDelete")
            if(isDelete == true)
                Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show()
        }
    }
}
