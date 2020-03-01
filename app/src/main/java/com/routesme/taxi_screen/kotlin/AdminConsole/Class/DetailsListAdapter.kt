package com.routesme.taxi_screen.kotlin.AdminConsole.Class

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.Model.ActionCell
import com.routesme.taxi_screen.kotlin.Model.DetailCell
import com.routesme.taxi_screen.kotlin.Model.ICell
import com.routesme.taxi_screen.kotlin.Model.LabelCell
import com.routesme.taxiscreen.R

class DetailsListAdapter(private val list: List<ICell>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LABEL = 0
        private const val TYPE_DETAIL = 1
        private const val TYPE_ACTION = 2
        private const val TYPE_DETAIL_ACTION = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_LABEL -> LabelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.label_cell_row, parent, false))
        TYPE_DETAIL -> DetailViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.detail_cell_row, parent, false))
        TYPE_ACTION -> ActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.action_cell_row, parent, false))
        else -> DetailActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.detail_action_cell_row, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        TYPE_LABEL -> onBindLabel(holder, list[position])
        TYPE_DETAIL -> onBindDetail(holder, list[position])
        TYPE_ACTION -> onBindAction(holder, list[position])
        else -> onBindDetailAction(holder, list[position])
    }

    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int = when (list[position]) {
        is LabelCell -> TYPE_LABEL
        is DetailCell -> TYPE_DETAIL
        is ActionCell -> TYPE_ACTION
        else -> TYPE_DETAIL_ACTION
    }
}