package com.routesme.taxi.LocationTrackingService.Database

import com.routesme.taxi.LocationTrackingService.Model.LocationFeed

class TrackingDatabaseHelperImpl(private val trackingDatabase: TrackingDatabase): TrackingDatabaseHelper {

    override suspend fun getFeeds(): List<LocationFeed> = trackingDatabase.locationFeedsDao().getFeeds()

    override suspend fun deleteFeeds(id1: Int, id2: Int) = trackingDatabase.locationFeedsDao().deleteFeeds(id1,id2)

    override suspend fun insertLocation(locationFeed: LocationFeed) = trackingDatabase.locationFeedsDao().insertLocation(locationFeed)

}