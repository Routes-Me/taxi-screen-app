package com.routesme.taxi.LocationTrackingService.Class

import android.database.sqlite.SQLiteDatabaseCorruptException
import android.location.Location
import android.util.Log
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
class TrackingDataLayer() {
    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val locationFeedsDao = trackingDatabase.locationFeedsDao()
    fun insertLocation(location: Location) {
        try {
            locationFeedsDao.insertLocation(getLocationFeed(location))
        }catch (e: Exception){
            Log.d("insert-location-exception","$e")
        }
    }
    private fun getLocationFeed(location: Location) = LocationFeed(latitude =  location.latitude, longitude = location.longitude, timestamp = System.currentTimeMillis()/1000)

    fun getFeeds() = locationFeedsDao.getFeeds()
    fun deleteFeeds(firstId: Int, lastId: Int) {
        try {
        locationFeedsDao.deleteFeeds(firstId,lastId)
        }catch (e: Exception){
            Log.d("delete-location-exception","$e")
        }
    }
}