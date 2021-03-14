package com.routesme.taxi.viewmodel

import android.content.Context
import android.util.Log
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

    fun getContent(offset: Int, limit: Int, context: Context): LiveData<ContentResponse>?{
        if (contentResponse == null){
            Log.d("ContentViewModel", "Get remote data")
            contentResponse = ContentRepository(context).getContent(offset,limit)
        }
        return contentResponse
    } //= ContentRepository(context).getContent(offset,limit)

    fun postReport(context: Context,data: JsonArray,deviceId: String) = ReportRepository(context, data).postReport(data,deviceId)

    fun unlinkDevice(vehicleId:String,deviceId:String,context: Context) = UnlinkRepository(context).unlink(vehicleId,deviceId)
}

/*
class MyViewModel : ViewModel() {

    // Stored cached bitmap.
    private var cachedBitmap: Bitmap? = null

    // Retrieves the image.
    fun getImage(): Bitmap {
        // If the image is not already cached, download it and cache it.
        if (cachedBitmap == null) {
            cachedBitmap = downloadImage()
        }

        return cachedBitmap
    }

    // Downloads image from the web.
    private fun downloadImage() : Bitmap {
        ...
    }

}
 */