package com.example.lab.noteapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lab.noteapp.R
import java.util.*

@Entity

data class Note(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    var title:String?,
    var text:String?,
    var image:String?,
    var gps:String?,
    var colorRes: Int = R.color.color1,
    var alarm: Long?
)