package com.routesme.vehicles.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationFeeds")
data class LocationFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "latitude") var latitude: Double, @ColumnInfo(name = "longitude") var longitude: Double, @ColumnInfo(name = "bearing") var bearing: Float, @ColumnInfo(name = "bearingAccuracyDegrees") var bearingAccuracyDegrees: Float, @ColumnInfo(name = "timestamp") var timestamp: Long ) {
    val coordinate: LocationCoordinate
        get() = LocationCoordinate(latitude, longitude, bearing, bearingAccuracyDegrees, timestamp)
}

data class LocationCoordinate(val latitude: Double, val longitude: Double, val bearing: Float, val bearingAccuracyDegrees: Float, val timestamp: Long)



data class BusLocationCoordinate(val BusID: String, val Longitude: Double, val Latitude: Double, val heading: Float, val headingAccuracy: Float)
