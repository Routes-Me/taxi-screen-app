package com.routesme.taxi_screen.Tracking.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.routesme.taxi_screen.Tracking.model.Tracking;

import java.util.List;

@Dao
public interface TrackingDao {

    @Query("SELECT * FROM Tracking ORDER BY ID")
    List<Tracking> loadAllLocations();

    @Insert
    void insertLocation(Tracking tracking);

    @Update
    void updateLocation(Tracking tracking);

    @Delete
    void delete(Tracking tracking);

    @Query("SELECT * FROM Tracking WHERE id = :id")
    Tracking loadLocationById(int id);

    //retrieve first tracking
    @Query("SELECT * FROM Tracking ORDER BY id ASC LIMIT 1")
    Tracking loadFirstLocation();

    //retrieve last tracking
    @Query("SELECT * FROM Tracking ORDER BY id DESC LIMIT 1")
    Tracking loadLastLocation();

    //Delete Tracking Data ...
    @Query("DELETE FROM Tracking")
    void deleteTrackingData();

}
