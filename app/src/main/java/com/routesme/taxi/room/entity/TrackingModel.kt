package com.routesme.taxi.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationFeeds")
data class LocationFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "latitude") var latitude: Double, @ColumnInfo(name = "longitude") var longitude: Double, @ColumnInfo(name = "timestamp") var timestamp: Long) {
    val coordinate: LocationCoordinate
        get() = LocationCoordinate(latitude, longitude, timestamp)
}

data class LocationCoordinate(val latitude: Double, val longitude: Double, val timestamp: Long)
