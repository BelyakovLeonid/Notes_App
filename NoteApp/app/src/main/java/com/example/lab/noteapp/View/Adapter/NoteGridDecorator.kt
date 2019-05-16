package com.example.lab.noteapp.View.Adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NoteGridDecorator(val dimen : Int) : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        with(outRect) {
            if (parent.getChildAdapterPosition(view) in 0..1) {
                top = dimen
            }

            if (parent.getChildAdapterPosition(view).rem(2) == 0) {
                left =  dimen
            }

            right = dimen
            bottom = dimen
        }
    }

}