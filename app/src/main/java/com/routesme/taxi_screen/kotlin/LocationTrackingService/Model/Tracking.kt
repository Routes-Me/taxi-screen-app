package com.routesme.taxi_screen.kotlin.LocationTrackingService.Model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tracking")
data class Tracking(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @Embedded var location: TrackingLocation,
        @ColumnInfo(name = "timestamp") var timestamp: String
)