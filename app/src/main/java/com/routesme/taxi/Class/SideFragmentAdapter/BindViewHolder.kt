package com.routesme.taxi.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import android.text.SpannedString
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.BarcodeFormat
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.uplevels.App
import net.codecision.glidebarcode.model.Barcode
import com.routesme.taxi.R

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
        val data = cell.data
        val promotion = data.promotion
        val promotionColors = data.promotionColors
        promotion?.let {
            val link = it.link
            if (!link.isNullOrEmpty()){
                val color = ThemeColor(promotionColors).getColor()
                val lowOpacityColor = ColorUtils.setAlphaComponent(color,33)

                //Here.. set the shadow color of video promotion card as a [lowOpacityColor]

                promotion.logoUrl?.let { logoUrl ->
                    glide.load(logoUrl).apply(imageOptions).into(videoLogoImage)
                    videoLogoImage.visibility = View.VISIBLE
                }
                if (!promotion.title.isNullOrEmpty()) title.text = promotion.title
                subTitle.text = getSubtitle(promotion.subtitle, promotion.code, color)
                generateQrCode(link,color).let {qrCode ->
                    glide.load(qrCode).into(qrCodeImage)
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

fun getSubtitle(subtitle: String?, code: String?, color: Int): SpannedString {
    return buildSpannedString {
        if (!subtitle.isNullOrBlank()){
            append(subtitle)
        }
        if (!code.isNullOrEmpty()){
            if (!subtitle.isNullOrEmpty()) append(", ")
            bold { color(color) { append("Use code ") } }
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

fun onBindWifi() {}

fun onBindBannerDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderBannerDiscount
    cell as BannerDiscountCell
    holder.apply {
        val data = cell.data
        val promotion = cell.data.promotion
        val promotionColors = data.promotionColors

        promotion?.let {
            it.link?.let {link ->
                val color = ThemeColor(promotionColors).getColor()
                generateQrCode(link,color).let {qrCode ->
                    glide.load(qrCode).into(qrCodeImage)
                }
            }
        }
    }
}

private fun generateQrCode(promotionLink: String, color: Int): Barcode {
    return Barcode(promotionLink, BarcodeFormat.QR_CODE,color,Color.TRANSPARENT)
}