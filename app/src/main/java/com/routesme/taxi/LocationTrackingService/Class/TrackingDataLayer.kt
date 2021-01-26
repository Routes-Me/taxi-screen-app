package com.routesme.taxi.LocationTrackingService.Class

import android.location.Location
import androidx.lifecycle.ViewModelProvider
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabaseHelperImpl
import com.routesme.taxi.LocationTrackingService.Database.TrackingRoomDBViewModel
import com.routesme.taxi.LocationTrackingService.Database.TrackingViewModelFactory
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
class TrackingDataLayer() {

  //  private lateinit var viewModel: TrackingRoomDBViewModel

    companion object {
        @get:Synchronized
        var instance = TrackingDataLayer()

        val viewModel =  ViewModelProvider(App.instance,TrackingViewModelFactory(TrackingDatabaseHelperImpl(TrackingDatabase.invoke(App.instance)))).get(TrackingRoomDBViewModel::class.java)
    }

    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val locationFeedsDao = trackingDatabase.locationFeedsDao()
    fun insertLocation(location: Location) {
            locationFeedsDao.insertLocation(getLocationFeed(location))
    }
    private fun getLocationFeed(location: Location) = LocationFeed(latitude =  location.latitude, longitude = location.longitude, timestamp = System.currentTimeMillis()/1000)

    fun getFeeds() = locationFeedsDao.getFeeds()

    fun getAllFeeds() = locationFeedsDao.getAllFeeds()

    fun deleteFeeds(firstId: Int, lastId: Int) {
        locationFeedsDao.deleteFeeds(firstId,lastId)
    }
}