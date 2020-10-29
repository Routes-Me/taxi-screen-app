package com.routesme.taxi.Class.SideFragmentAdapter


import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi.R


class ViewHolderEmptyVideoDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.image)
}

class ViewHolderVideoDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.titleTv)
    val subTitle: TextView = itemView.findViewById(R.id.subTitleTv)
    val qrCodeImage: ImageView = itemView.findViewById(R.id.videoQrCodeImage)
    var card = itemView.findViewById(R.id.videoBannerView) as CardView



}

class ViewHolderLargeEmpty(itemView: View) : RecyclerView.ViewHolder(itemView) {}

class ViewHolderDate(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val clockTv: TextView = itemView.findViewById(R.id.clockTv)
    val dayTv: TextView = itemView.findViewById(R.id.dayTv)
}

class ViewHolderSmallEmpty(itemView: View) : RecyclerView.ViewHolder(itemView) {}

class ViewHolderWifi(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameTv: TextView = itemView.findViewById(R.id.nameTv)
    val passwordTv: TextView = itemView.findViewById(R.id.passwordTv)
}

class ViewHolderBannerDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val qrCodeImage: ImageView = itemView.findViewById(R.id.bannerQrCodeImage)
}



