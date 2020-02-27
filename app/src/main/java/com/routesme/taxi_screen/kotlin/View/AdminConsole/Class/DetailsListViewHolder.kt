package com.routesme.taxi_screen.kotlin.View.AdminConsole.Class

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.View.AdminConsole.Model.AdminConsoleLists
import com.routesme.taxiscreen.R

class DetailsListViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.detail_cell_row, parent, false)) {
    private var title: TextView? = null
    private var value: TextView? = null
    private var splitLine: View? = null

    init {
        title = itemView.findViewById(R.id.title)
        value = itemView.findViewById(R.id.value)
        splitLine = itemView.findViewById(R.id.splitLine)
    }

    fun bind(detailCell: AdminConsoleLists.DetailCell) {
        title?.text = detailCell.title
        value?.text = detailCell.value
        splitLine?.visibility = when(detailCell.splitLine){true -> View.VISIBLE ; else -> View.INVISIBLE}
    }
}