package com.routesme.taxi.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.routesme.taxi.data.repository.ContentRepository
import com.routesme.taxi.data.repository.ReportRepository
import com.routesme.taxi.data.repository.UnlinkRepository

class ContentViewModel : ViewModel() {

    fun getContent(offset: Int, limit: Int, context: Context) = ContentRepository(context).getContent(offset,limit)

    fun postReport(context: Context,data: JsonArray,deviceId: String) = ReportRepository(context, data).postReport(data,deviceId)

    fun unlinkDevice(vehicleId:String,deviceId:String,context: Context) = UnlinkRepository(context).unlink(vehicleId,deviceId)

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel","Oncleared Called")
    }
}