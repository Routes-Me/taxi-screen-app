package com.routesme.taxi.LocationTrackingService.Class

import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.uplevels.App

class VideoSaverDataLayer{

    private val videoTrackingDatabase = TrackingDatabase.invoke(App.instance)
    private val videoSaverDao = videoTrackingDatabase.videoSaverDeo()

    fun insertData(){
        videoSaverDao.getVideoList()
    }

}