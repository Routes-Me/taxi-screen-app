package com.routesme.taxi_screen.Tracking.model;



import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


import java.sql.Date;

@Entity(tableName = "Tracking")
public class Tracking {

    @PrimaryKey(autoGenerate = true)
    int id;

    @Embedded
    private TrackingLocation location;

    @ColumnInfo(name = "timestamp")
    private String timestamp;



    //Constructor...

    @Ignore
    public Tracking(TrackingLocation location, String timestamp) {
        this.location = location;
        this.timestamp = timestamp;
    }

    public Tracking(int id, TrackingLocation location, String timestamp) {
        this.id = id;
        this.location = location;
        this.timestamp = timestamp;
    }




    //Getter...

    public int getId() {
        return id;
    }

    public TrackingLocation getLocation() {
        return location;
    }

    public String getTimestamp() {
        return timestamp;
    }


    //Setter...


    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(TrackingLocation location) {
        this.location = location;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }





}


