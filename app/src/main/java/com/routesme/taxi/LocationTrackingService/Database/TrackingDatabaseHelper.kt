package com.routesme.taxi.LocationTrackingService.Database

import com.routesme.taxi.LocationTrackingService.Model.LocationFeed

interface TrackingDatabaseHelper {

    suspend fun getFeeds(): List<LocationFeed>

    suspend fun deleteFeeds(id1:Int,id2:Int)

    suspend fun insertLocation(locationFeed: LocationFeed)

}