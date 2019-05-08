package com.example.lab.noteapp.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lab.noteapp.Model.Note

@Database(
    entities = [Note::class],
    exportSchema = false,
    version = 1
)
abstract class NoteDB: RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    companion object{

        private var instance: NoteDB? = null

        const val NOTE_DB_NAME = "note.db"

        fun getInstance(context: Context): NoteDB{
            if(instance == null){
                synchronized(NoteDB::class){
                    if(instance == null){
                        instance = Room.databaseBuilder(
                            context,
                            NoteDB::class.java,
                            NOTE_DB_NAME
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}