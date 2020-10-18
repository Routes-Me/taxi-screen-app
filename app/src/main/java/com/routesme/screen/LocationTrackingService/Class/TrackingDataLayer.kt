package com.routesme.screen.LocationTrackingService.Class

import android.location.Location
import com.routesme.screen.uplevels.App
import com.routesme.screen.LocationTrackingService.Database.TrackingDatabase
import com.routesme.screen.LocationTrackingService.Model.LocationFeed
import org.json.JSONArray

class TrackingDataLayer() {
    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val locationFeedsDao = trackingDatabase.locationFeedsDao()

    fun insertLocation(location: Location) {
        locationFeedsDao.insertLocation(getLocationFeed(location))
    }
    private fun getLocationFeed(location: Location) = LocationFeed(latitude =  location.latitude, longitude = location.longitude, timestamp = System.currentTimeMillis()/1000)


    fun getFeeds(): JSONArray? {
        val feeds = locationFeedsDao.getResults()

        return if (!feeds.isNullOrEmpty()){
            getJsonArray(feeds)
        }else{
            null
        }
    }

    private fun getJsonArray(locationFeeds: List<LocationFeed>):JSONArray {
        return JSONArray().apply {
            for (l in locationFeeds){
                put(l.toJSON())
            }
        }
    }
}