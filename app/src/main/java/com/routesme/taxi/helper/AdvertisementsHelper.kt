package com.routesme.taxi.helper

import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.routesme.taxi.App
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
            val exoPlayerCacheSize: Long = free / 5
            val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
            val exoDatabaseProvider = ExoDatabaseProvider(App.instance)
            return SimpleCache(instance.file, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
        }
    }

    fun deleteCache(){

        file.delete()
    }
}