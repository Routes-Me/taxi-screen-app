package com.routesme.taxi.view.adapter

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
import com.routesme.taxi.R
import com.routesme.taxi.data.model.Data
import com.routesme.taxi.helper.ThemeColor
import net.codecision.glidebarcode.model.Barcode

class WifiAndQRCodeAdapter(context: Context, list: List<Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context: Context = context
    var list: List<Data> = list
    private inner class View1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bannerQrCodeImage : ImageView = itemView.findViewById(R.id.bannerQrCodeImage)
        fun bind(position: Int) {
            val recyclerViewModel = list[position]
            val promotion = recyclerViewModel.promotion
            val tintColor = recyclerViewModel.tintColor
            promotion.let {
                it?.link?.let {link ->
                    val color = ThemeColor(tintColor).getColor()
                    generateQrCode(link,color).let {

                        Glide.with(context).load(it).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)).into(bannerQrCodeImage)

                    }
                }
            }
        }
    }

    private inner class View2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (list[viewType].promotion != null) {
            return View1ViewHolder(LayoutInflater.from(context).inflate(R.layout.banner_discount_cell, parent, false))
        }
        return View2ViewHolder(LayoutInflater.from(context).inflate(R.layout.wifi_cell, parent, false))

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
    private fun generateQrCode(promotionLink: String, color: Int): Barcode {
        return Barcode(promotionLink, BarcodeFormat.QR_CODE,color, Color.TRANSPARENT)
    }

}