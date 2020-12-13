package com.routesme.taxi.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi.LocationTrackingService.Model.VideoTracking

@Dao
interface VideoTrackingDeo {

    @Transaction
    @Query("SELECT * FROM tbl_video_tracking")
    fun getVideoList(): List<VideoTracking>

    @Transaction
    @Query("SELECT id,COUNT(advertisement_id) as count, advertisement_id,device_id,timestamp,length,media_type FROM tbl_video_tracking GROUP BY advertisement_id")
    fun getVideoListAnalyticsReport(): List<VideoTracking>

    /*@Transaction
    @Query("DELETE FROM LocationFeeds WHERE id BETWEEN :id2 AND :id1")
    fun deleteFeeds(id1:Int,id2:Int)
    @Transaction*/
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVideoTrackingDetails(videoTracking: VideoTracking)
}