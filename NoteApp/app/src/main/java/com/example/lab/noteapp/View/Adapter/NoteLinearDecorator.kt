package com.example.lab.noteapp.View.Adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NoteLinearDecorator(val dimen : Int) : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = dimen
            }
            left =  dimen
            right = dimen
            bottom = dimen
        }
    }

}