package com.routesme.taxi_screen.kotlin.SideFragmentAdapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxiscreen.R

class ViewHolderDate(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val clockTv: TextView = itemView.findViewById(R.id.clockTv)
    val weekDayTv: TextView = itemView.findViewById(R.id.weekDayTv)
    val monthDayTv: TextView = itemView.findViewById(R.id.monthDayTv)
}

class ViewHolderWifi(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameTv: TextView = itemView.findViewById(R.id.nameTv)
    val passwordTv: TextView = itemView.findViewById(R.id.passwordTv)
}

class ViewHolderDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val detailsTv: TextView = itemView.findViewById(R.id.detailsTv)
    val qrCodeImage: ImageView = itemView.findViewById(R.id.qrCodeImage)
}