package com.routesme.taxi.LocationTrackingService.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.routesme.taxi.LocationTrackingService.Model.VideoTracking

@Dao
interface VideoTrackingDeo {

    @Transaction
    @Query("SELECT * FROM tbl_video_tracking")
    fun getVideoList(): List<VideoTracking>

    /*@Transaction
    @Query("DELETE FROM LocationFeeds WHERE id BETWEEN :id2 AND :id1")
    fun deleteFeeds(id1:Int,id2:Int)
    @Transaction*/
    @Insert
    fun insertVideoTrackingDetails(videoTracking: VideoTracking)
}