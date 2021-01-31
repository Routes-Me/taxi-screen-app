package com.routesme.taxi.LocationTrackingService.Class

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.LocationTrackingService.Model.LocationJsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlinx.serialization.Serializable

@Serializable
data class LocationFeedsMessage(val feeds: List<LocationFeed>) {
    //@Serializable
   // data class Data(val balance: String)

    val message: JSONObject
        get() {
            val feedsArray = JsonArray().apply {
                for (feed in feeds){
                    add(LocationJsonObject(feed).toJSON())
                }
            }
            Log.d("LocationFeedsMessage","feedsArray: $feedsArray")
            return JSONObject().put("SendLocation", JSONArray(feedsArray.toString()))
        }
}

/*
class TrackingServiceHelper {

    companion object {
        @get:Synchronized
        var instance = TrackingServiceHelper()

    }

    fun getFeedsJsonArray(feeds: List<LocationFeed>): JsonArray? {
        return if (!feeds.isNullOrEmpty()) {
            getJsonArray(feeds)
        } else {
            null
        }
    }

    private fun getJsonArray(locationFeeds: List<LocationFeed>): JsonArray {
        val locationJsonArray = JsonArray()
        for (l in locationFeeds) {
            val locationJsonObject: JsonObject = LocationJsonObject(l).toJSON()
            locationJsonArray.add(locationJsonObject)
        }
        return locationJsonArray
    }

    fun getMessage(messageFeeds: String): String? {
        val messageObject = JSONObject()
        val feedsArray = JSONArray(messageFeeds)
        try {
            messageObject.put("SendLocation", feedsArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return messageObject.toString()
    }
}
*/