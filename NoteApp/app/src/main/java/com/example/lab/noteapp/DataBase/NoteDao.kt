package com.example.lab.noteapp.DataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.lab.noteapp.Model.Note

@Dao
interface NoteDao {

    @Insert(onConflict = REPLACE )
    fun addNote(note: Note)

    @Update
    fun editNote(note: Note)

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun getNoteById(noteId: Int): LiveData<Note>

    @Query("SELECT * FROM note")
    fun getAll(): LiveData<Array<Note>>

    @Query("DELETE FROM note")
    fun deleteALL()

    @Query("DELETE FROM note WHERE id = :noteId")
    fun deleteById(noteId: Int)
}