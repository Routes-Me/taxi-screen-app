package com.routesme.taxi.LocationTrackingService.Class

import android.util.Log
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Model.VideoTracking
import com.routesme.taxi.MVVM.Model.VideoTrackingModel
import com.routesme.taxi.uplevels.App

class TrackingVideoLayer(){

    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val trackingVideoDeo = trackingDatabase.videoTrackingDeo()
    private var arrayList:ArrayList<VideoTrackingModel>?=null
    private var item = VideoTrackingModel()
    fun insertVideoItem(videoTracking: VideoTracking){

        trackingVideoDeo.insertVideoTrackingDetails(videoTracking)

    }

    /*fun getVideoList() : ArrayList<VideoTrackingModel>{

        trackingVideoDeo.getVideoList().forEach(){

            Log.d("Media item","ID : :${it.video_id}")
            Log.d("Media item","Time : :${it.timestamp}")
            var videoTrackingModel = VideoTrackingModel()
            videoTrackingModel.video_id = it.video_id.toString()
            videoTrackingModel.id = it.id.toString()
            videoTrackingModel.time_stamp = it.timestamp
            arrayList!!.add(videoTrackingModel)


        }
        return arrayList!!

    }*/

    /*fun getVideoList() : VideoTrackingModel{

        trackingVideoDeo.getVideoList().forEach(){
            item.id = it.id.toString()
            item.video_id = it.video_id.toString()
            item.time_stamp = it.timestamp
            //Log.d("Media item","ID : :${it.id}, Video Id Count ${it.video_id}, Timestamp ${it.timestamp}")
           // Log.d("Media item","Time : :${it.timestamp}")
            /*var videoTrackingModel = VideoTrackingModel()
            videoTrackingModel.video_id = it.video_id.toString()
            videoTrackingModel.id = it.id.toString()
            videoTrackingModel.time_stamp = it.timestamp
            arrayList?.add(videoTrackingModel)*/

        }
        return item
        //return arrayList!!
    }*/

    fun getVideoAnalyticsReport() : VideoTrackingModel{

        trackingVideoDeo.getVideoListAnalyticsReport().forEach {

            /*item.id = it.id.toString()
            item.video_id = it.advertisement_id.toString()
            item.time_stamp = it.timestamp.toString()*/

        }
        return item
    }
}