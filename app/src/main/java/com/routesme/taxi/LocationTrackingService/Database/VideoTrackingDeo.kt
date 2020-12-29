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
    @Transaction
    @Query("SELECT id, COUNT(advertisementId) as count,deviceId, advertisementId,createdAt,length,mediaType FROM tbl_video_tracking WHERE createdAt BETWEEN :from_date AND :to_date    GROUP BY advertisementId,createdAt")
    fun getVideoAnalaysisReport(from_date: String,to_date:String):List<VideoTracking>
    
    @Insert
    fun insertVideoTrackingDetails(videoTracking: VideoTracking)

    @Query("DELETE FROM tbl_video_tracking WHERE createdAt BETWEEN :from_date and :to_date")
    fun deleteTable(from_date: String,to_date: String): Int




}