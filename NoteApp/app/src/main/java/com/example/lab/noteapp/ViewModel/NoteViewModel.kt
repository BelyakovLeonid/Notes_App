package com.example.lab.noteapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.Repository.NotesRepo

class NoteViewModel: ViewModel() {
    private lateinit var notes: LiveData<Array<Note>>

    init {
        refreshNotes()
    }

    fun refreshNotes(){
        notes = NotesRepo.getNotes()
    }

    fun addNote(note: Note){
        NotesRepo.addNote(note)
        refreshNotes()
    }

    fun deleteAll(){
        NotesRepo.deleteAll()
        refreshNotes()
    }

    fun deleteById(id: Int){
        NotesRepo.deleteById(id)
        refreshNotes()
    }

    fun editNote(note: Note){
        NotesRepo.editNote(note)
        refreshNotes()
    }

    fun getAllNotes() = notes

    fun getNoteById(id: Int) = NotesRepo.getNoteById(id)

    fun searchNotes(search: String) = NotesRepo.searchNotes(search)

}