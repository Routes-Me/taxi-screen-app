package com.routesme.taxi.LocationTrackingService.Class

import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Model.VideoTracking
import com.routesme.taxi.uplevels.App

class TrackingVideoLayer(){

    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val trackingVideoDeo = trackingDatabase.videoTrackingDeo()

    fun insertVideoItem(videoTracking: VideoTracking){

        trackingVideoDeo.insertVideoTrackingDetails(videoTracking)

    }

    fun getVideoList(){

        trackingVideoDeo.getVideoList()
    }
}