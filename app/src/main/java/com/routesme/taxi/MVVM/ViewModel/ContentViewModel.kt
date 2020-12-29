package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.routesme.taxi.MVVM.Repository.ContentRepository
import com.routesme.taxi.MVVM.Repository.ReportRepository
import com.routesme.taxi.MVVM.Repository.UnlinkRepository
import org.json.JSONObject

class ContentViewModel() : ViewModel() {
    fun getContent(offset: Int, limit: Int, context: Context) = ContentRepository(context).getContent(offset,limit)

    fun postReport(context: Context,data: JSONObject,deviceId: String) = ReportRepository(context,data).postReport(data,deviceId)

    fun unlinkDevice(vehicleId:String,deviceId:String,context: Context) = UnlinkRepository(context).unlink(vehicleId,deviceId)
}