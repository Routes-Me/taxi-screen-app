package com.routesme.taxi_screen.AdminConsolePanel.Class

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxiscreen.R

class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.title)
}

class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.title)
    val value: TextView = itemView.findViewById(R.id.value)
    val splitLine: View = itemView.findViewById(R.id.splitLine)
}

class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val action: TextView = itemView.findViewById(R.id.action)
}

class DetailActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.title)
    val status: TextView = itemView.findViewById(R.id.status)
    val action: TextView = itemView.findViewById(R.id.action)
}