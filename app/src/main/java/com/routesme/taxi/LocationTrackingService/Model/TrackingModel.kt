package com.routesme.taxi.LocationTrackingService.Model

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject

@Entity(tableName = "LocationFeeds")
class LocationFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "latitude") var latitude: Double, @ColumnInfo(name = "longitude") var longitude: Double, @ColumnInfo(name = "timestamp") var timestamp: Long) {
    val location: Location
        get() {
            val location = Location("provider")
            location.latitude = latitude
            location.longitude = longitude
            return location
        }
}

class LocationJsonObject(private val locationFeed: LocationFeed) {
    fun toJSON(): JsonObject {
        val jo = JsonObject()
        jo.addProperty("latitude", locationFeed.latitude)
        jo.addProperty("longitude", locationFeed.longitude)
        jo.addProperty("timestamp", locationFeed.timestamp.toString())
        return jo
    }
}

@Entity(tableName = "tbl_advertisement_tracking")
class AdvertisementTracking(@PrimaryKey(autoGenerate = true) var id:Int = 0,@ColumnInfo(name = "advertisementId") var advertisementId: Int,@ColumnInfo(name = "date") var date: Long,@ColumnInfo(name = "morning") var morning:Int,@ColumnInfo(name = "noon") var noon:Int,@ColumnInfo(name = "evening") var evening:Int,@ColumnInfo(name = "night") var night:Int,@ColumnInfo(name = "time_in_day") var time_in_day:Long,@ColumnInfo(name = "media_type") var media_type:String)
