package com.routesme.taxi_screen.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.Class.Helper
import com.routesme.taxi_screen.Class.QRCodeHelper
import com.routesme.taxi_screen.MVVM.Model.*
import com.routesme.taxiscreen.R
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

private val UserAppBaseUrl = Helper.getConfigValue("UserAppBaseUrl", R.raw.config)

fun onBindEmptyVideoDiscount() {}

fun onBindVideoDiscount(activity: Activity, holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderVideoDiscount
    cell as VideoDiscountCell
    holder.apply {
        val qrCode = cell.promotion
        if (qrCode != null) {
            if (!qrCode.title.isNullOrEmpty()) title.text = qrCode.title
            if (!qrCode.subtitle.isNullOrEmpty()) subTitle.text = qrCode.subtitle
            if (!qrCode.logoUrl.isNullOrEmpty() && !qrCode.promotionId.isNullOrEmpty()) {
               // val logo = loadImageFromURL(qrCode.logoUrl,"logo")
                //val image =
                        generateQrCode(qrCode.promotionId,135,135,qrCode.logoUrl,qrCodeImage)
                //qrCodeImage.setImageBitmap(image)
            }
        }
    }
}

fun loadImageFromURL(Url: String?, name: String?): Bitmap? {
    return try {
      //  val `is`: InputStream = URL(url).content as InputStream
       //Drawable.createFromStream(`is`, name).toBitmap()

        val url = URL(Url)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val inputStream: InputStream = urlConnection.inputStream
       BitmapFactory.decodeStream(inputStream)

    } catch (e: Exception) {
        null
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
        if (qrCode != null) {
            if (!qrCode.logoUrl.isNullOrEmpty()) {
                // Glide.with(App.instance).load(Uri.parse(qrCode.url)).apply(App.imageOptions).into(qrCodeImage)
              //  Utils.fetchSvg(activity, qrCode.logoUrl, qrCodeImage)
                // GlideToVectorYou.justLoadImage(activity, Uri.parse(qrCode.url), qrCodeImage)
                generateQrCode(qrCode.promotionId,135,135,qrCode.logoUrl,qrCodeImage)
            }
        }
    }
}

private fun generateQrCode(promotionId: String?, width: Int, height: Int, logoUrl: String?, qrCodeImage: ImageView) {

    val builder = Uri.Builder()
    builder.scheme("https")
            .authority(UserAppBaseUrl)
            .appendPath("promotions")
            .appendPath(promotionId)
    val promotionUrl: String = builder.build().toString()

    return QRCodeHelper
            .newInstance(App.instance)
            .setWidthAndHeight(width,height)
            .setContent(promotionUrl)
            .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
            .setMargin(2)
            .getQRCOde(logoUrl,qrCodeImage)
}