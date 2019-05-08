package com.example.lab.noteapp.View

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.R
import com.example.lab.noteapp.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity: AppCompatActivity()  {
    private lateinit var noteViewModel: NoteViewModel
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.lab.noteapp.R.layout.activity_detail)
        setSupportActionBar(toolbar)

        noteId = intent.getIntExtra("noteId", -1)

        //получаем viewModel
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        noteViewModel.getNoteById(noteId).observe(this, Observer{
           if(it != null)
               refreshUI(it)
        })
    }

    fun refreshUI(note: Note){
        titleView.text = note.title
        textView.text = note.text
        imageView.setImageURI(note.image?.toUri())
        layout.setBackgroundResource(note.colorRes)
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
                    it.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                    startActivity(it)
                }
                finish()
                true
            }
            R.id.action_delete ->{
                noteViewModel.deleteById(noteId)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}