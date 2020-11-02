package com.routesme.taxi.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.Class.QRCodeHelper
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.R
import com.routesme.taxi.uplevels.App

private val UserAppBaseUrl = Helper.getConfigValue("UserAppBaseUrl", R.raw.config)

fun onBindEmptyVideoDiscount(holder: RecyclerView.ViewHolder, activity: FragmentActivity?) {
    holder as ViewHolderEmptyVideoDiscount
    holder.apply {
        val metrics = DisplayMetrics()
        val windowManager = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.defaultDisplay?.getMetrics(metrics)
        var screenWidth = (metrics.widthPixels * 69) / 100
        empty_cardview.layoutParams = ConstraintLayout.LayoutParams(screenWidth, MATCH_PARENT)
    }

}

fun onBindVideoDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell, activity: FragmentActivity?) {
    holder as ViewHolderVideoDiscount
    cell as VideoDiscountCell
    holder.apply {
        val qrCode = cell.promotion
        if (qrCode != null) {
            if (!qrCode.title.isNullOrEmpty()) title.text = qrCode.title
            if (!qrCode.subtitle.isNullOrEmpty()) subTitle.text = qrCode.subtitle
            if (!qrCode.promotionId.isNullOrEmpty()) {
                val promotionType = if (qrCode.redirectLink != null) PromotionType.Links else PromotionType.Promotions
                generateQrCode(promotionType, qrCode.promotionId, activity).let {
                    qrCodeImage.setImageBitmap(it)
                }
            }
        }
        val metrics = DisplayMetrics()
        val windowManager = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.defaultDisplay?.getMetrics(metrics)
        var screenWidth = (metrics.widthPixels * 69) / 100
        Log.d("Width", screenWidth.toString())
        Log.d("Height", metrics.heightPixels.toString())
        Log.d("Width", metrics.widthPixels.toString())
        card.layoutParams = ConstraintLayout.LayoutParams(screenWidth, MATCH_PARENT)
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

fun onBindBannerDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell, activity: FragmentActivity?) {
    holder as ViewHolderBannerDiscount
    cell as BannerDiscountCell
    holder.apply {
        val qrCode = cell.promotion
        if (qrCode != null) {
            if (!qrCode.promotionId.isNullOrEmpty()) {
                val promotionType = if (qrCode.redirectLink != null) PromotionType.Links else PromotionType.Promotions
                generateQrCode(promotionType, qrCode.promotionId, activity).let {
                    qrCodeImage.setImageBitmap(it)
                }

            }
        }
    }
}

private fun generateQrCode(promotionType: PromotionType, promotionId: String?, activity: FragmentActivity?): Bitmap {
    val promotionUrl = getPromotionUrl(promotionType, promotionId)
    Log.d("Promotion",promotionUrl)
    return qrCodeGenerator
            .setActivity(activity)
            .setContent(promotionUrl)
            .qrcOde
}

fun getPromotionUrl(promotionType: PromotionType, promotionId: String?): String {
    val builder = Uri.Builder()
    builder.scheme("http")
            .authority(UserAppBaseUrl)
            .appendPath(promotionType.value)
            .appendPath(promotionId)
    return builder.build().toString()
}

private val qrCodeGenerator = QRCodeHelper
        .newInstance(App.instance)
        .setWidthAndHeight(180, 180)
        .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
        .setMargin(2)

enum class PromotionType(val value: String) { Links("links"), Promotions("promotions") }

