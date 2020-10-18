package com.routesme.screen.LocationTrackingService.Database

import androidx.room.*
import com.routesme.screen.LocationTrackingService.Model.LocationFeed

//Location Feeds Table
@Dao
interface LocationFeedsDao {

    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 100")
    fun getFeeds(): List<LocationFeed>

    @Transaction
    //@Query("DELETE FROM LocationFeeds WHERE id <= :id LIMIT 100")
   // @Query("DELETE FROM LocationFeeds WHERE id = :id ORDER BY id DESC LIMIT 100")
    @Query("DELETE FROM LocationFeeds WHERE id = :id ORDER BY id DESC LIMIT 100")
    fun deleteFeeds(id:Int)

    @Transaction
    @Insert
    fun insertLocation(locationFeed: LocationFeed)
}