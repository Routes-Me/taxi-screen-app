package com.routesme.screen.Class

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


object Utils {
    private var httpClient: OkHttpClient? = null
    fun fetchSvg(context: Context, url: String): Bitmap? {
        if (httpClient == null) { // Use cache for performance and basic offline capability
            httpClient = OkHttpClient.Builder()
                    .cache(Cache(context.cacheDir, 5 * 1024 * 1014))
                    .build()
        }

        val bitmap = getBitmapFromURL(url)
     return bitmap
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getStream(url: String) : InputStream? {
        var inputStream: InputStream? = null
        val request: Request = Request.Builder().url(url).build()
        httpClient!!.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                // target.setImageResource(R.drawable.ic_sun)
                inputStream = null
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful && response.body() != null){
                    val stream: InputStream = response.body()!!.byteStream()
                    //Sharp.loadInputStream(stream).into(target)
                    inputStream = stream
                    stream.close()
                }else{
                    inputStream = null
                }
            }
        })
        return inputStream
    }
}