package com.example.lab.noteapp.Repository

import androidx.lifecycle.LiveData
import com.example.lab.noteapp.App
import com.example.lab.noteapp.Model.Note
import kotlinx.coroutines.*

object NotesRepo {

    private val noteDao by lazy{
        App.noteDB.getNoteDao()
    }

    fun getNoteById(id: Int): LiveData<Note> = noteDao.getNoteById(id)

    fun getNotes(): LiveData<Array<Note>> = noteDao.getAll()

    fun searchNotes(search: String): LiveData<Array<Note>> = noteDao.searchNotes(search)

    fun addNote(note: Note): Long = runBlocking {
        withContext(Dispatchers.Default){ noteDao.addNote(note)}
    }

    fun deleteAll(){
        GlobalScope.launch {
            noteDao.deleteALL()
        }.start()
    }

    fun deleteById(id: Int){
        GlobalScope.launch{
            noteDao.deleteById(id)
        }.start()
    }

    fun editNote(note: Note){
        GlobalScope.launch{
            noteDao.editNote(note)
        }.start()
    }

    fun refreshDB(){
        //тут будем обновлять бд при каждом запуске приложения
    }

}