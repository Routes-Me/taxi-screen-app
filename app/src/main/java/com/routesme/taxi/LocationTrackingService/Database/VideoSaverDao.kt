package com.routesme.taxi.LocationTrackingService.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.LocationTrackingService.Model.VideoFeed

@Dao
interface VideoSaverDao {
    @Transaction
    @Query("SELECT * FROM VideoSaver ORDER BY download_id DESC LIMIT 100")
    fun getVideoList(): List<VideoFeed>

    @Transaction
    @Insert
    fun insertValue(videoFeed:VideoFeed)

}