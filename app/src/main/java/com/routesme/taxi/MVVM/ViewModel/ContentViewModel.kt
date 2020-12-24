package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.routesme.taxi.MVVM.Repository.ContentRepository
import com.routesme.taxi.MVVM.Repository.ReportRepository
import com.routesme.taxi.MVVM.Repository.UnlinkRepository

class ContentViewModel() : ViewModel() {
    fun getContent(offset: Int, limit: Int, context: Context) = ContentRepository(context).getContent(offset,limit)

    fun postReport(context: Context,data: JsonArray) = ReportRepository(context,data).postReport(data)

    fun unlinkDevice(vehicleId:String,deviceId:String,context: Context) = UnlinkRepository(context).unlink(vehicleId,deviceId)
}