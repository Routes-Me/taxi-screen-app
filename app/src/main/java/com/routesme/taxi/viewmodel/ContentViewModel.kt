package com.routesme.taxi.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.routesme.taxi.data.model.ContentResponse
import com.routesme.taxi.data.repository.ContentRepository
import com.routesme.taxi.data.repository.ReportRepository
import com.routesme.taxi.data.repository.UnlinkRepository

class ContentViewModel : ViewModel() {
    private var contentResponse: MutableLiveData<ContentResponse>? = null

    fun getContent(offset: Int, limit: Int, context: Context): LiveData<ContentResponse>? {
        if (contentResponse?.value?.data == null) {
            contentResponse = ContentRepository(context).getContent(offset, limit)
        }
        return contentResponse
    }

    fun postReport(context: Context, data: JsonArray, deviceId: String) = ReportRepository(context, data).postReport(data, deviceId)

    fun unlinkDevice(vehicleId: String, deviceId: String, context: Context) = UnlinkRepository(context).unlink(vehicleId, deviceId)

    override fun onCleared() {
        super.onCleared()
    }
}