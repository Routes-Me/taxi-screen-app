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
    @Query("SELECT id, COUNT(advertisement_id) as count,device_id, advertisement_id,date_time,length,media_type FROM tbl_video_tracking GROUP BY advertisement_id")
    fun getVideoAnalaysisReport():List<VideoTracking>
    
    @Insert
    fun insertVideoTrackingDetails(videoTracking: VideoTracking)

    @Query("DELETE FROM tbl_video_tracking")
    fun deleteTable(): Int

    /*@Query("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='tbl_video_tracking'")
    fun resetTable(): Int*/
}