package com.routesme.taxi_screen.LocationTrackingService.Model

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject

@Entity(tableName = "LocationFeeds")
data class LocationFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "latitude") var latitude: Double, @ColumnInfo(name = "longitude") var longitude: Double, @ColumnInfo(name = "timestamp") var timestamp: Long) {
    val location: Location
        get() {
            val location = Location("provider")
            location.latitude = latitude
            location.longitude = longitude
            return location
        }
}

@Entity(tableName = "MessageFeeds")
data class MessageFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "message") var message: String)

class LocationJsonObject(private val locationFeed: LocationFeed) {
    fun toJSON(): JsonObject {
        val jo = JsonObject()
        jo.addProperty("latitude", locationFeed.latitude)
        jo.addProperty("longitude", locationFeed.longitude)
        jo.addProperty("timestamp", locationFeed.timestamp.toString())
        return jo
    }
}