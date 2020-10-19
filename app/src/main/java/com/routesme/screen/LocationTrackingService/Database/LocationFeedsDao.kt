package com.routesme.screen.LocationTrackingService.Database

import androidx.room.*
import com.routesme.screen.LocationTrackingService.Model.LocationFeed

@Dao
interface LocationFeedsDao {

    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 100")
    fun getFeeds(): List<LocationFeed>

    @Transaction
    @Query("DELETE FROM LocationFeeds WHERE id BETWEEN :id2 AND :id1")
    fun deleteFeeds(id1:Int,id2:Int)

    @Transaction
    @Insert
    fun insertLocation(locationFeed: LocationFeed)
}