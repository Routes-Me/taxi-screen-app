package com.routesme.taxi.Class.SideFragmentAdapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.RelativeLayout
import com.routesme.taxi.R

class ViewHolderEmptyVideoDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.image)
    val empty_cardview = itemView.findViewById(R.id.emptyCardView) as RelativeLayout
}

class ViewHolderVideoDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val videoLogoImage: ImageView = itemView.findViewById(R.id.videoLogoImage)
    val title: TextView = itemView.findViewById(R.id.titleTv)
    val subTitle: TextView = itemView.findViewById(R.id.subTitleTv)
    val qrCodeImage: ImageView = itemView.findViewById(R.id.videoQrCodeImage)
    val card = itemView.findViewById(R.id.videoPromotionCard) as RelativeLayout
    val cardShadow = itemView.findViewById(R.id.videoPromotionShadow) as RelativeLayout

}

class ViewHolderLargeEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)

class ViewHolderDate(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val clockTv: TextView = itemView.findViewById(R.id.clockTv)
    val dayTv: TextView = itemView.findViewById(R.id.dayTv)
}

class ViewHolderSmallEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)

class ViewHolderWifi(itemView: View) : RecyclerView.ViewHolder(itemView)

class ViewHolderBannerDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val qrCodeImage: ImageView = itemView.findViewById(R.id.bannerQrCodeImage)
}



