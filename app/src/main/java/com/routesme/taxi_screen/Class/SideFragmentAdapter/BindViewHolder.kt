package com.routesme.taxi_screen.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.StreamEncoder
import com.caverock.androidsvg.SVG
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.MVVM.Model.*
import java.io.InputStream


fun onBindEmptyVideoDiscount() {}

fun onBindVideoDiscount(activity: Activity, holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderVideoDiscount
    cell as VideoDiscountCell
    holder.apply {
        val qrCode = cell.qrCode
        if (qrCode != null){
            if (!qrCode.title.isNullOrEmpty()) title.text = qrCode.title
            if (!qrCode.subTitle.isNullOrEmpty()) subTitle.text = qrCode.subTitle
            if (!qrCode.url.isNullOrEmpty()) Glide.with(App.instance).load(Uri.parse(qrCode.url)).apply(App.imageOptions).into(qrCodeImage)
        }
    }
}

fun onBindLargeEmpty() {}

@SuppressLint("SetTextI18n")
fun onBindDate(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderDate
    cell as DateCell
    holder.apply { clockTv.text = cell.clock; dayTv.text = "${cell.weekDay}\n ${cell.monthDay}" }
}

fun onBindSmallEmpty() {}

fun onBindWifi(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderWifi
    cell as WifiCell
    holder.apply {
        nameTv.text = cell.name; passwordTv.text = cell.password
    }
}

fun onBindBannerDiscount(activity: Activity, holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderBannerDiscount
    cell as BannerDiscountCell
    holder.apply {
        val qrCode = cell.qrCode
        if (qrCode != null){
            if (!qrCode.url.isNullOrEmpty()) {
                Glide.with(App.instance).load(Uri.parse(qrCode.url)).apply(App.imageOptions).into(qrCodeImage)
            }
        }
    }
}






