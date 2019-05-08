package com.example.lab.noteapp.View.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.lab.noteapp.Model.Note
import kotlinx.android.synthetic.main.note_item.view.*



class NoteAdapter(
    private val notes: Array<Note>,
    val context: Context,
    val clickListener: (Note) -> Unit): RecyclerView.Adapter<NoteAdapter.NoteHolder>() {


    class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var item: CardView? = null
        var title: TextView? = null
        var text: TextView? = null
        var image: ImageView? = null

        init {
            item = itemView as CardView
            title = itemView.title
            text = itemView.text
            image = itemView.imageView
        }

        fun bind(note: Note, context: Context, clickListener: (Note) -> Unit){
            item?.setOnClickListener { clickListener(note) }
            title?.text = note.title
            text?.text = note.text
            image?.setImageURI(note.image?.toUri())
            item?.setCardBackgroundColor(ContextCompat.getColor(context,note.colorRes))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(com.example.lab.noteapp.R.layout.note_item, parent, false)
        return NoteHolder(itemView)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(notes[position], context, clickListener)
    }

}