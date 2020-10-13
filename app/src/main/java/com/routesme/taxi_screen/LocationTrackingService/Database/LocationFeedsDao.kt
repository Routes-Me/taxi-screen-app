package com.routesme.taxi_screen.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi_screen.LocationTrackingService.Model.LocationFeed

//Location Feeds Table
@Dao
interface LocationFeedsDao {

    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 100")
    fun getResults(): List<LocationFeed>

    @Transaction
    @Insert
    fun insertLocation(locationFeed: LocationFeed)

    @Transaction
    @Update
    fun updateLocation(locationFeed: LocationFeed)

    @Transaction
    @Delete
    fun delete(locationFeed: LocationFeed)

    @Transaction
    @Query("SELECT * FROM LocationFeeds WHERE id LIKE :id")
    fun loadLocationById(id:Int) : LocationFeed

    //retrieve first LocationFeed
    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id ASC LIMIT 1")
    fun loadFirstLocation(): LocationFeed

    //retrieve last LocationFeed
    @Transaction
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 1")
    fun loadLastLocation(): LocationFeed

    //Delete LocationFeed Data ...
    @Transaction
    @Query("DELETE FROM LocationFeeds WHERE id <= :id")
    fun clearLocationFeedsTable(id:Int)
}