package com.routesme.taxi_screen.kotlin.AdminConsolePanel.Class

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.Model.*

fun onBindLabel(holder: RecyclerView.ViewHolder, cell: ICell) {
    holder as LabelViewHolder
    cell as LabelCell
    holder.apply { title.text = cell.title }
}

fun onBindDetail(holder: RecyclerView.ViewHolder, cell: ICell) {
    holder as DetailViewHolder
    cell as DetailCell
    holder.apply {
        title.text = cell.title; value.text = cell.value
        if (cell.splitLine) {
            holder.splitLine.visibility = View.VISIBLE
        }
    }
}

fun onBindAction(holder: RecyclerView.ViewHolder, cell: ICell) {
    holder as ActionViewHolder
    cell as ActionCell
    holder.apply { action.text = cell.action }
}

fun onBindDetailAction(holder: RecyclerView.ViewHolder, cell: ICell) {
    holder as DetailActionViewHolder
    cell as DetailActionCell
    holder.apply {
        title.text = cell.title; value.text = cell.value
        if (cell.splitLine) {
            holder.splitLine.visibility = View.VISIBLE
        }
    }
}