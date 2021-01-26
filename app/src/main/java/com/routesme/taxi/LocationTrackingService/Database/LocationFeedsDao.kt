package com.routesme.taxi.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed

@Dao
interface LocationFeedsDao {

    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 100")
    suspend fun getFeeds(): List<LocationFeed>
/*
    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id")
    suspend fun getAllFeeds(): List<LocationFeed>
*/

    @Query("DELETE FROM LocationFeeds WHERE id BETWEEN :id2 AND :id1")
    suspend fun deleteFeeds(id1:Int,id2:Int)

    @Insert
    suspend fun insertLocation(locationFeed: LocationFeed)
}