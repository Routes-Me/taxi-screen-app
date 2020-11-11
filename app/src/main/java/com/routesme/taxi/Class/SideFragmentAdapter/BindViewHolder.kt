package com.routesme.taxi.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.routesme.taxi.Class.AdvertisementsHelper
import com.routesme.taxi.Class.Helper
import com.routesme.taxi.Class.QRCodeHelper
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.R
import com.routesme.taxi.uplevels.App

val glide = Glide.with(App.instance)
val imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)

fun onBindEmptyVideoDiscount(holder: RecyclerView.ViewHolder, activity: FragmentActivity?) {
    holder as ViewHolderEmptyVideoDiscount
    holder.apply {
        val metrics = DisplayMetrics()
        val windowManager = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getMetrics(metrics)
        val screenWidth = (metrics.widthPixels * 69) / 100
        empty_cardview.layoutParams = ConstraintLayout.LayoutParams(screenWidth, MATCH_PARENT)
    }

}

@SuppressLint("SetTextI18n")
fun onBindVideoDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell, activity: FragmentActivity?) {
    holder as ViewHolderVideoDiscount
    cell as VideoDiscountCell
    holder.apply {
        val promotion = cell.promotion

        promotion?.let {
            val link = it.link
            if (!link.isNullOrEmpty()){
                promotion.logoUrl?.let { logoUrl ->
                    glide.load(logoUrl).apply(imageOptions).into(videoLogoImage)
                    videoLogoImage.visibility = View.VISIBLE
                }
                if (!promotion.title.isNullOrEmpty()) title.text = promotion.title
                subTitle.text = getSubtitle(promotion.subtitle, promotion.code)
                generateQrCode(link, activity).let {bitmap ->
                    qrCodeImage.setImageBitmap(bitmap)
                }
            }
        }

        val metrics = DisplayMetrics()
        val windowManager = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getMetrics(metrics)
        val screenWidth = (metrics.widthPixels * 69) / 100
        Log.d("Width", screenWidth.toString())
        Log.d("Height", metrics.heightPixels.toString())
        Log.d("Width", metrics.widthPixels.toString())
        card.layoutParams = ConstraintLayout.LayoutParams(screenWidth, MATCH_PARENT)
    }
}

fun getSubtitle(subtitle: String?, code: String?): SpannedString {
    return buildSpannedString {
        if (!subtitle.isNullOrBlank()){
            append(subtitle)
        }
        if (!code.isNullOrEmpty()){
            if (!subtitle.isNullOrEmpty()) append(", ")
            bold { color(ContextCompat.getColor(App.instance, R.color.routes_color)) { append("Use code ") } }
            append(code)
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

fun onBindBannerDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell, activity: FragmentActivity?) {
    holder as ViewHolderBannerDiscount
    cell as BannerDiscountCell
    holder.apply {
        val promotion = cell.promotion

        promotion?.let {
            it.link?.let {link ->
                generateQrCode(link, activity).let {bitmap ->
                    qrCodeImage.setImageBitmap(bitmap)
                }
            }
        }
    }
}

private fun generateQrCode(promotionLink: String, activity: FragmentActivity?): Bitmap {
    Log.d("promotionLink", promotionLink)
    return qrCodeGenerator
            .setActivity(activity)
            .setContent(promotionLink)
            .qrcOde

}

private val qrCodeGenerator = QRCodeHelper
        .newInstance(App.instance)
        .setWidthAndHeight(180, 180)
        .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
        .setMargin(2)

enum class PromotionType(val value: String) { Links("links"), Places("places"), Promotions("promotions") }

