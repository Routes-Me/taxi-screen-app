package com.routesme.vehicles.helper

import android.util.Log
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.routesme.vehicles.App
import java.io.File

class AdvertisementsHelper {
    val file = File(App.instance.cacheDir, "routes_video")

    companion object {
        @get:Synchronized
        val instance = AdvertisementsHelper()
        val simpleCache = initializeVideoCaching()
        private fun initializeVideoCaching(): SimpleCache {
            val maxMemory = Runtime.getRuntime().maxMemory()
            val freeMemory = Runtime.getRuntime().freeMemory()
            val totalMemory = Runtime.getRuntime().totalMemory()
            val used = totalMemory - freeMemory
            val free = maxMemory - used
            Log.d("FreeMemory","${free}")
            val exoPlayerCacheSize: Long = free / 2
            //val exoPlayerCacheSize: Long = 100 * 1024 * 1024
            val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
            val exoDatabaseProvider = ExoDatabaseProvider(App.instance)
            return SimpleCache(instance.file, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
        }
    }

    fun deleteCache() : Boolean {
       return file.deleteRecursively()
    }
}