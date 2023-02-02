package com.routesme.vehicles.view.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannedString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.BarcodeFormat
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.Data
import com.routesme.vehicles.helper.ThemeColor
import com.routesme.vehicles.room.entity.LocationCoordinate
import com.routesme.vehicles.service.receiver.LocationReceiver
import net.codecision.glidebarcode.model.Barcode

class BottomBannerAdapter(context: Context, list: List<Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_BANNER = 1
        const val VIEW_TYPE_EMPTY = 2
    }

    private val context: Context = context
    var list: List<Data> = list

    private inner class View1ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var titleTv: TextView = itemView.findViewById(R.id.titleTv)
        var subTitleTv: TextView = itemView.findViewById(R.id.subTitleTv)
        var videoPromotionCard: RelativeLayout = itemView.findViewById(R.id.videoPromotionCard)
        var videoLogoImage: ImageView = itemView.findViewById(R.id.videoLogoImage)
        var videoQrCodeImage: ImageView = itemView.findViewById(R.id.videoQrCodeImage)
        fun bind(position: Int) {
            val recyclerViewModel = list[position]
            val promotion = recyclerViewModel.promotion
            val tintColor = recyclerViewModel.tintColor
            val color = ThemeColor(tintColor).getColor()
            promotion?.let {
                videoPromotionCard.setElevationShadowColor(color)
                if (!it.link.isNullOrEmpty()) {
                    generateQrCode(it.link, LocationReceiver.instance.currentLocationCoordinate, color).let { qrCode ->
                        Glide.with(context).load(qrCode).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)).into(videoQrCodeImage)
                    }
                }
                if (promotion.logoUrl != null) {
                    Glide.with(context).load(promotion.logoUrl).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)).into(videoLogoImage)
                    videoLogoImage.visibility = View.VISIBLE
                } else videoLogoImage.visibility = View.GONE
                if (!promotion.title.isNullOrEmpty()) titleTv.text = promotion.title
                if (!promotion.subtitle.isNullOrEmpty()) subTitleTv.text = getSubtitle(promotion.subtitle, promotion.code, color)
            }
        }
    }

    private inner class View2ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (list[viewType].promotion != null) {
            return View1ViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.video_discount_cell, parent, false)
            )
        }
        return View2ViewHolder(

                LayoutInflater.from(context).inflate(R.layout.empty_video_discount_cell, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].promotion != null) {
            (holder as View1ViewHolder).bind(position)
        } else {
            (holder as View2ViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun generateQrCode(promotionLink: String, locationCoordinate: LocationCoordinate, color: Int): Barcode {
        val link = promotionLink + "?lat=${locationCoordinate.latitude}&lng=${locationCoordinate.longitude}"
        Log.d("PromotionQRCode", "Video .. generatedQRCode link: $link")
        return Barcode(link, BarcodeFormat.QR_CODE, color, Color.TRANSPARENT)
    }

    private fun getSubtitle(subtitle: String?, code: String?, color: Int): SpannedString {
        return buildSpannedString {
            if (!subtitle.isNullOrBlank()) {
                append(subtitle)
            }
            if (!code.isNullOrEmpty()) {
                if (!subtitle.isNullOrEmpty()) append(", ")
                bold { color(color) { append("Use code ") } }
                append(code)
            }
        }
    }
}