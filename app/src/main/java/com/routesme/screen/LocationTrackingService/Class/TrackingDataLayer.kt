package com.routesme.screen.LocationTrackingService.Class

import android.location.Location
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.screen.uplevels.App
import com.routesme.screen.LocationTrackingService.Database.TrackingDatabase
import com.routesme.screen.LocationTrackingService.Model.LocationFeed

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TrackingDataLayer() {
    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val locationFeedsDao = trackingDatabase.locationFeedsDao()


    fun insertLocation(location: Location){
        locationFeedsDao.insertLocation(getLocationFeed(location))
    }

    private fun getLocationFeed(location: Location) = LocationFeed(latitude =  location.latitude, longitude = location.longitude, timestamp = System.currentTimeMillis()/1000)

    private fun getLocationsResult(): String? {
      val results = locationFeedsDao.getResults()
        return if (results.isNullOrEmpty()) {
            null
        }else {
            results.toString()
        }
    }

    fun getJsonArray(locationFeeds: List<LocationFeed>): List<JsonObject> {
        return locationFeeds.map { it.toJSON() }
    }
}