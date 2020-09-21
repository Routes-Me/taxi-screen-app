package com.routesme.taxi_screen.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.Class.Utils
import com.routesme.taxi_screen.MVVM.Model.*


fun onBindEmptyVideoDiscount() {}

fun onBindVideoDiscount(activity: Activity, holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderVideoDiscount
    cell as VideoDiscountCell
    holder.apply {
        val qrCode = cell.promotion
        if (qrCode != null){
            if (!qrCode.title.isNullOrEmpty()) title.text = qrCode.title
            if (!qrCode.subtitle.isNullOrEmpty()) subTitle.text = qrCode.subtitle
            if (!qrCode.logoUrl.isNullOrEmpty()) {
                //Glide.with(App.instance).load(Uri.parse(qrCode.url)).apply(App.imageOptions).into(qrCodeImage)
                Utils.fetchSvg(activity, qrCode.logoUrl, qrCodeImage)
               // GlideToVectorYou.justLoadImage(activity, Uri.parse(qrCode.url), qrCodeImage)
            }
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
        val qrCode = cell.promotion
        if (qrCode != null){
            if (!qrCode.logoUrl.isNullOrEmpty()) {
               // Glide.with(App.instance).load(Uri.parse(qrCode.url)).apply(App.imageOptions).into(qrCodeImage)
               Utils.fetchSvg(activity, qrCode.logoUrl, qrCodeImage)
               // GlideToVectorYou.justLoadImage(activity, Uri.parse(qrCode.url), qrCodeImage)
            }
        }
    }
}