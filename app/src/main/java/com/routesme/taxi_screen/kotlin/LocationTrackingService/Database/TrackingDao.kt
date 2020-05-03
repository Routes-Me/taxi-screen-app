package com.routesme.taxi_screen.kotlin.LocationTrackingService.Database

import androidx.room.*
import com.routesme.taxi_screen.kotlin.Model.VehicleLocation


@Dao
interface TrackingDao {

    @Query("SELECT * FROM Tracking ORDER BY ID")
    fun loadAllLocations(): List<VehicleLocation>

    @Insert
    fun insertLocation(vehicleLocation: VehicleLocation)

    @Update
    fun updateLocation(vehicleLocation: VehicleLocation)

    @Delete
    fun delete(vehicleLocation: VehicleLocation)

    @Query("SELECT * FROM Tracking WHERE id LIKE :id")
    fun loadLocationById(id:Int) : VehicleLocation

    //retrieve first tracking
    @Query("SELECT * FROM Tracking ORDER BY id ASC LIMIT 1")
    fun loadFirstLocation(): VehicleLocation

    //retrieve last tracking
    @Query("SELECT * FROM Tracking ORDER BY id DESC LIMIT 1")
    fun loadLastLocation(): VehicleLocation

    //Delete Tracking Data ...
    @Query("DELETE FROM Tracking")
    fun clearTrackingData()
}