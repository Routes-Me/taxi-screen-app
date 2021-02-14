package com.routesme.taxi.Class.SideFragmentAdapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.R
import net.codecision.glidebarcode.model.Barcode


class WifiQrCodeAdapter(context: Context,list: List<Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    companion object {
        const val TYPE_WIFI = 0
        const val TYPE_QRCODE = 1
    }
    private val context: Context = context
    var list: List<Data> = list
    inner class ViewHolderWIFI(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindItem(position: Int){

                Log.d("Item","${position}")

            }
    }

    inner class ViewHolderQrCode(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var qrcodeImage: ImageView = itemView.findViewById(R.id.bannerQrCodeImage)
        fun bindItem(position: Int) {
            val promotion = list[position].promotion
            val tintColor = list[position].tintColor
            promotion?.let {
                it.link?.let { link ->
                    val color = ThemeColor(tintColor).getColor()
                    generateQrCode(link,color).let {qrCode ->
                        Glide.with(qrcodeImage.context).load(qrCode).into(qrcodeImage)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {

        if (position == TYPE_WIFI) return ViewHolderWIFI(LayoutInflater.from(context).inflate(R.layout.wifi_cell, parent, false))
        else return ViewHolderQrCode(LayoutInflater.from(context).inflate(R.layout.banner_discount_cell, parent, false))

    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (list[position].promotion == null) {
            Log.d("Promotion","WIFI")
            (holder as ViewHolderWIFI).bindItem(position)
        } else {
            Log.d("Promotion","QRCODE")
            (holder as ViewHolderQrCode).bindItem(position)
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if(list[position].promotion == null){
            Log.d("Promotion","TYPE_WIFI")
            TYPE_WIFI
        } else{
            Log.d("Promotion","TYPE_QRCODE")
            TYPE_QRCODE
        }

    }

    private fun generateQrCode(promotionLink: String, color: Int): Barcode {
        return Barcode(promotionLink, BarcodeFormat.QR_CODE,color, Color.TRANSPARENT)
    }

}