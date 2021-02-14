package com.routesme.taxi.Class.SideFragmentAdapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.BarcodeFormat
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.Data
import com.routesme.taxi.R
import net.codecision.glidebarcode.model.Barcode

class VideoBannerAdapter(context: Context, list: List<Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    companion object {
        const val TYPE_EMPTY = 0
        const val TYPE_BANNER = 1
    }
    private val context: Context = context
    var list: List<Data> = list
    inner class ViewHolderWIFI(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(position: Int){

            //Log.d("Item","${position}")

        }
    }

    inner class ViewHolderQrCode(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var qrcodeImage: ImageView = itemView.findViewById(R.id.videoQrCodeImage)
        var logoImg: ImageView = itemView.findViewById(R.id.videoLogoImage)
        var textViewTitle:TextView = itemView.findViewById(R.id.titleTv)
        var textViewSubTitle:TextView = itemView.findViewById(R.id.subTitleTv)
        var cardShow: RelativeLayout = itemView.findViewById(R.id.videoPromotionShadow)
        fun bindItem(position: Int) {
            val promotion = list[position].promotion
            val tintColor = list[position].tintColor
            promotion?.let {
                val link = it.link
                if (!link.isNullOrEmpty()){
                    val color = ThemeColor(tintColor).getColor()
                    cardShow.setElevationShadowColor(color)
                    promotion.logoUrl?.let { logoUrl ->
                        Glide.with(logoImg.context).load(logoUrl).apply(imageOptions).into(logoImg)
                        logoImg.visibility = View.VISIBLE
                    }
                    if (!promotion.title.isNullOrEmpty()) textViewTitle.text = promotion.title
                    textViewSubTitle.text = getSubtitle(promotion.subtitle, promotion.code, color)
                    generateQrCode(link,color).let {qrCode ->
                        Glide.with(qrcodeImage.context).load(qrCode).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(qrcodeImage)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {

        if (position == TYPE_EMPTY) return ViewHolderWIFI(LayoutInflater.from(context).inflate(R.layout.empty_video_discount_cell, parent, false))
        else return ViewHolderQrCode(LayoutInflater.from(context).inflate(R.layout.video_discount_cell, parent, false))

    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (list[position].promotion == null) {
            Log.d("Promotion","EMPTY")
            (holder as ViewHolderWIFI).bindItem(position)
        } else {
            Log.d("Promotion","VIDEO")
            (holder as ViewHolderQrCode).bindItem(position)
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if(list[position].promotion == null){
            Log.d("Promotion","TYPE_EMPTY")
            TYPE_EMPTY
        } else{
            Log.d("Promotion","TYPE_BANNER")
            TYPE_BANNER
        }

    }

    private fun generateQrCode(promotionLink: String, color: Int): Barcode {
        return Barcode(promotionLink, BarcodeFormat.QR_CODE,color, Color.TRANSPARENT)
    }

}