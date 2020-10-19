package com.routesme.screen.LocationTrackingService.Database

import androidx.room.*
import com.routesme.screen.LocationTrackingService.Model.LocationFeed

@Dao
interface LocationFeedsDao {

    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 10")
    fun getFeeds(): List<LocationFeed>

    @Transaction
    @Query("DELETE FROM LocationFeeds WHERE id >= :id-9 AND id <= :id")
   // @Query("DELETE FROM LocationFeeds WHERE id IN (SELECT id FROM LocationFeeds WHERE id <= :id ORDER BY id DESC LIMIT 10)")
    fun deleteFeeds(id:Int)

    @Transaction
    @Insert
    fun insertLocation(locationFeed: LocationFeed)
}