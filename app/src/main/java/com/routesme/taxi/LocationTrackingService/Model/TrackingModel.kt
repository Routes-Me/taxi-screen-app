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

@Entity(tableName = "tbl_video_tracking")
class VideoTracking(@PrimaryKey(autoGenerate = true) var id:Int = 0,@ColumnInfo(name = "device_id") var device_id:Int,@ColumnInfo(name = "advertisement_id") var advertisement_id:Int,@ColumnInfo(name = "date_time") var date_time:String,@ColumnInfo(name = "count") var count:Int,@ColumnInfo(name = "length") var length:Int,@ColumnInfo(name = "media_type") var media_type:String)

//@Entity(tableName = "MessageFeeds")
//class MessageFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "message") var message: String)

class LocationJsonObject(private val locationFeed: LocationFeed) {
    fun toJSON(): JsonObject {
        val jo = JsonObject()
        jo.addProperty("latitude", locationFeed.latitude)
        jo.addProperty("longitude", locationFeed.longitude)
        jo.addProperty("timestamp", locationFeed.timestamp.toString())
        return jo
    }
}

class VideoJsonObject(private val videoTracking: VideoTracking) {
    fun toJSON(): JsonObject {
        val jo = JsonObject()
        jo.addProperty("advertisementId", videoTracking.advertisement_id)
        jo.addProperty("deviceId", videoTracking.device_id)
        jo.addProperty("createdAt", videoTracking.date_time)
        jo.addProperty("count", videoTracking.count)
        jo.addProperty("mediaType", videoTracking.media_type)
        jo.addProperty("length", videoTracking.length)
        return jo
    }
}