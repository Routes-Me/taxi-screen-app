package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.routesme.taxi.LocationTrackingService.Model.VideoTracking
import com.routesme.taxi.MVVM.Repository.ContentRepository
import com.routesme.taxi.MVVM.Repository.ReportRepository

class ContentViewModel() : ViewModel() {
    fun getContent(offset: Int, limit: Int, context: Context) = ContentRepository(context).getContent(offset,limit)

    fun postReport(context: Context,data: JsonArray) = ReportRepository(context,data).postReport(data)
}