package com.routesme.taxi_screen.kotlin.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Model.Tracking


@Dao
interface TrackingDao {

    @Query("SELECT * FROM Tracking ORDER BY ID")
    fun loadAllLocations(): List<Tracking>

    @Insert
    fun insertLocation(tracking: Tracking)

    @Update
    fun updateLocation(tracking: Tracking)

    @Delete
    fun delete(tracking: Tracking)

    @Query("SELECT * FROM Tracking WHERE id LIKE :id")
    fun loadLocationById(id:Int) : Tracking

    //retrieve first tracking
    @Query("SELECT * FROM Tracking ORDER BY id ASC LIMIT 1")
    fun loadFirstLocation(): Tracking

    //retrieve last tracking
    @Query("SELECT * FROM Tracking ORDER BY id DESC LIMIT 1")
    fun loadLastLocation(): Tracking


    //Delete Tracking Data ...
    @Query("DELETE FROM Tracking")
    fun clearTrackingData()

}