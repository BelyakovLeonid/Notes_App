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
    suspend fun addNote(note: Note): Long

    @Update
    fun editNote(note: Note)

    @Query("SELECT * FROM note WHERE rowid = :noteId")
    fun getNoteById(noteId: Int): LiveData<Note>

    @Query("SELECT * FROM note WHERE title LIKE :search OR text LIKE :search")
    fun searchNotes(search: String):LiveData<Array<Note>>

    @Query("SELECT * FROM note")
    fun getAll(): LiveData<Array<Note>>

    @Query("SELECT * FROM note")
    fun getAllSync(): Array<Note>

    @Query("SELECT * FROM note WHERE rowid = :noteId")
    fun getNoteSyinc(noteId: Int): Note

    @Query("DELETE FROM note")
    fun deleteALL()

    @Query("DELETE FROM note WHERE rowid = :noteId")
    fun deleteById(noteId: Int)
}