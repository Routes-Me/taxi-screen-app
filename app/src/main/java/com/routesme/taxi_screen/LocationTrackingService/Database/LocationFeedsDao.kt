package com.routesme.taxi_screen.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi_screen.LocationTrackingService.Model.LocationFeed

//Location Feeds Table
@Dao
interface LocationFeedsDao {

    @Query("SELECT * FROM LocationFeeds ORDER BY ID")
    fun loadAllLocations(): List<LocationFeed>

    @Insert
    fun insertLocation(locationFeed: LocationFeed)

    @Update
    fun updateLocation(locationFeed: LocationFeed)

    @Delete
    fun delete(locationFeed: LocationFeed)

    @Query("SELECT * FROM LocationFeeds WHERE id LIKE :id")
    fun loadLocationById(id:Int) : LocationFeed

    //retrieve first LocationFeed
    @Query("SELECT * FROM LocationFeeds ORDER BY id ASC LIMIT 1")
    fun loadFirstLocation(): LocationFeed

    //retrieve last LocationFeed
    @Query("SELECT * FROM LocationFeeds ORDER BY id DESC LIMIT 1")
    fun loadLastLocation(): LocationFeed

    //Delete LocationFeed Data ...
    @Query("DELETE FROM LocationFeeds WHERE id <= :id")
    fun clearLocationFeedsTable(id:Int)
}