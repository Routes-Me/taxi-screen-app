package com.routesme.taxi_screen.Class.SideFragmentAdapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxiscreen.R

class ViewHolderEmptyVideoDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.image)
}

class ViewHolderVideoDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val detailsTv: TextView = itemView.findViewById(R.id.titleTv)
    val qrCodeImage: ImageView = itemView.findViewById(R.id.videoQrCodeImage)
}

class ViewHolderLargeEmpty(itemView: View) : RecyclerView.ViewHolder(itemView) {}

class ViewHolderDate(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val clockTv: TextView = itemView.findViewById(R.id.clockTv)
    val weekDayTv: TextView = itemView.findViewById(R.id.weekDayTv)
    val monthDayTv: TextView = itemView.findViewById(R.id.monthDayTv)
}

class ViewHolderSmallEmpty(itemView: View) : RecyclerView.ViewHolder(itemView) {}

class ViewHolderWifi(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameTv: TextView = itemView.findViewById(R.id.nameTv)
    val passwordTv: TextView = itemView.findViewById(R.id.passwordTv)
}

class ViewHolderBannerDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val bannerQrCodeImage: ImageView = itemView.findViewById(R.id.bannerQrCodeImage)
}



