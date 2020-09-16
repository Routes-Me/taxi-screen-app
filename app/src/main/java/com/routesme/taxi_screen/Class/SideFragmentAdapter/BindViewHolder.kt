package com.routesme.taxi_screen.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.MVVM.Model.*

fun onBindEmptyVideoDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderEmptyVideoDiscount
    cell as EmptyVideoDiscountCell
    holder.apply {

    }
}

fun onBindVideoDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderVideoDiscount
    cell as VideoDiscountCell
    holder.apply {
        val qrCode = cell.qrCode
        if (qrCode != null){
            if (!qrCode.details.isNullOrEmpty()) detailsTv.text = qrCode.details;
            if (!qrCode.url.isNullOrEmpty()) Glide.with(App.instance).load(Uri.parse(qrCode.url)).apply(App.imageOptions).into(qrCodeImage)
        }
    }
}

fun onBindLargeEmpty(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {}

@SuppressLint("SetTextI18n")
fun onBindDate(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderDate
    cell as DateCell
    holder.apply { clockTv.text = cell.clock; weekDayTv.text = cell.weekDay; monthDayTv.text = cell.monthDay }
}

fun onBindSmallEmpty(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {}

fun onBindWifi(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderWifi
    cell as WifiCell
    holder.apply {
        nameTv.text = cell.name; passwordTv.text = cell.password
    }
}

fun onBindBannerDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderBannerDiscount
    cell as BannerDiscountCell
    holder.apply {
        val qrCode = cell.qrCode
        if (qrCode != null){
            if (!qrCode.url.isNullOrEmpty()) Glide.with(App.instance).load(Uri.parse(qrCode.url)).apply(App.imageOptions).into(bannerQrCodeImage)
        }
    }
}




