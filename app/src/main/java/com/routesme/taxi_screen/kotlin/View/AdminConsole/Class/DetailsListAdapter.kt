package com.routesme.taxi_screen.kotlin.View.AdminConsole.Class

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.View.AdminConsole.Model.AdminConsoleLists

class DetailsListAdapter(private val list: List<AdminConsoleLists.DetailCell>) : RecyclerView.Adapter<DetailsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DetailsListViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: DetailsListViewHolder, position: Int) {
        val detailCell: AdminConsoleLists.DetailCell = list[position]
        holder.bind(detailCell)
    }
    override fun getItemCount(): Int = list.size


}