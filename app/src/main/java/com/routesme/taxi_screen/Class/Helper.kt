package com.routesme.taxi_screen.Class

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.routesme.taxi_screen.uplevels.App
import java.io.IOException
import java.util.*

object Helper {
    private const val TAG = "Helper"
    fun getConfigValue(name: String, resourceId: Int): String? {
        val resources = App.instance.resources
        try {
            val rawResource = resources.openRawResource(resourceId)
            val properties = Properties()
            properties.load(rawResource)
            return properties.getProperty(name)
        } catch (e: NotFoundException) {
            Log.e(TAG, "Unable to find the config file: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open config file.")
        }
        return null
    }
}