package com.example.lab.noteapp.View

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab.noteapp.App
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.R
import com.example.lab.noteapp.View.Adapter.NoteAdapter
import com.example.lab.noteapp.View.Adapter.NoteGridDecorator
import com.example.lab.noteapp.View.Adapter.NoteLinearDecorator
import com.example.lab.noteapp.ViewModel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity: AppCompatActivity() {

    private var searchQuery: String? = null
    private val EDIT_NOTE_REQUEST = 2
    private var orientation = true
    private lateinit var menu: Menu
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        orientation = App.prefs!!.lastOrientation

        Log.d("MyTag", "orientation = ${orientation}")

        if(orientation) {
            resView.addItemDecoration(NoteLinearDecorator(resources.getDimension(R.dimen.recycler_margin).toInt()))
            resView.layoutManager = LinearLayoutManager(this)
        }else {
            resView.addItemDecoration(NoteGridDecorator(resources.getDimension(R.dimen.recycler_margin).toInt()))
            resView.layoutManager = GridLayoutManager(this, 2)
        }

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent!!)
    }

    fun handleIntent(intent: Intent){
        if(intent.action == Intent.ACTION_SEARCH){
            searchQuery = intent.getStringExtra(SearchManager.QUERY)
            doMySearch(searchQuery!!)
        }
    }

    fun doMySearch(query: String){
        noteViewModel.searchNotes(query).observe(this, Observer {
            refreshUI(it)
        })
    }

    fun refreshUI(notes: Array<Note>){
        noteAdapter = NoteAdapter(notes, this, {note: Note -> onNoteClick(note)})
        resView.adapter = noteAdapter

        val string = when(notes.size.rem(100)){
            11 -> "Найдено ${notes.size} заметок"
            else -> when(notes.size.rem(10)){
                1-> "Найдена ${notes.size} заметка"
                2,3,4 -> "Найдено ${notes.size} заметки"
                else -> "Найдено ${notes.size} заметок"
            }
        }

      Snackbar.make(layout, string, Snackbar.LENGTH_LONG).show()

    }

    fun onNoteClick(note: Note){
        Intent(this, DetailActivity::class.java).also {
            it.putExtra("noteId", note.id)
            startActivityForResult(it, EDIT_NOTE_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        this.menu = menu

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val itemSearch = menu.findItem(R.id.action_search).actionView as SearchView
        itemSearch.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        itemSearch.setIconifiedByDefault(false)

        //делаем так, чтобы в SearchView новой активности
        //сразу находился запрос из ainActivity
        menu.findItem(R.id.action_search).expandActionView()
        itemSearch.setQuery(searchQuery, false)
        itemSearch.clearFocus()

        //делаем так, чтобы SearchActivity автоматически
        //закрывалась при нажатии на стрелку назад
        menu.findItem(R.id.action_search).setOnActionExpandListener(
            object: MenuItem.OnActionExpandListener{
                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    finish()
                    return true
                }
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }
            }
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getNoteFromIntent(data: Intent?): Note {
        val title = data?.extras?.getString("title")
        val text = data?.extras?.getString("text")
        val image = data?.extras?.getString("image")
        val color = data?.extras?.getInt("color")

        val note = Note(null, title, text, image, null, color!!, null)

        return note
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK){
            noteViewModel.editNote(getNoteFromIntent(data))
        }

        if(requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_CANCELED){
            val isDelete = data?.extras?.getBoolean("isDelete")
            if(isDelete == true)
                Snackbar.make(layout, "Заметка удалена", Snackbar.LENGTH_SHORT).show()
        }
    }
}