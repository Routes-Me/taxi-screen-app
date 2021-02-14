package com.routesme.taxi.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorator(private val mSpace: Int) : RecyclerView.ItemDecoration() {
    fun RecyclerView.getItemOffsets(outRect: Rect, view: View?, state: RecyclerView.State?) {
        val position: Int = getChildAdapterPosition(view!!)
        if (position != 0) outRect.top = mSpace
    }

}