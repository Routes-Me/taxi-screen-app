package com.routesme.taxi_screen.Class.SideFragmentAdapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.MVVM.Model.DateCell
import com.routesme.taxi_screen.MVVM.Model.DiscountCell
import com.routesme.taxi_screen.MVVM.Model.ISideFragmentCell
import com.routesme.taxi_screen.MVVM.Model.WifiCell

@SuppressLint("SetTextI18n")
fun onBindDate(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderDate
    cell as DateCell
    holder.apply { clockTv.text = cell.clock; weekDayTv.text = cell.weekDay; monthDayTv.text = cell.monthDay }
}

fun onBindWifi(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderWifi
    cell as WifiCell
    holder.apply {
        itemView.animate().rotationX(360F).duration = 10000
        nameTv.text = cell.name; passwordTv.text = cell.password

        if (cell.qrCode != null){
            wifiView.visibility = View.GONE
            qrCodeView.visibility = View.VISIBLE
            if (!cell.qrCode.url.isNullOrEmpty()) Glide.with(App.instance).load(Uri.parse(cell.qrCode.url)).apply(App.imageOptions).into(qrCodeImage)
           // itemView.animate().rotationX(360F).duration = 10000
            Log.d("RecyclerView Changes","${cell.qrCode}")
        }else{
            wifiView.visibility = View.VISIBLE
            qrCodeView.visibility = View.GONE
           // itemView.animate().rotationX(360F).duration = 10000
        }
    }
}

fun onBindDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderDiscount
    cell as DiscountCell
    holder.apply {
        itemView.animate().rotationX(360F).duration = 10000
       // itemView.animate().rotationX(360F).duration = 3000
        if (!cell.details.isNullOrEmpty()) detailsTv.text = cell.details;
        if (!cell.url.isNullOrEmpty()) Glide.with(App.instance).load(Uri.parse(cell.url)).apply(App.imageOptions).into(qrCodeImage) }
}