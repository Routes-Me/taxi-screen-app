package com.routesme.taxi.LocationTrackingService.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationFeeds")
data class LocationFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "latitude") var latitude: Double, @ColumnInfo(name = "longitude") var longitude: Double, @ColumnInfo(name = "timestamp") var timestamp: Long) {
    val coordinate: LocationCoordinate
        get() = LocationCoordinate(latitude,longitude,timestamp)
}

data class LocationCoordinate(val latitude: Double, val longitude: Double, val timestamp: Long)

@Entity(tableName = "tbl_advertisement_tracking")
class AdvertisementTracking(@PrimaryKey(autoGenerate = true) var id:Int = 0,@ColumnInfo(name = "advertisementId") var advertisementId: Int,@ColumnInfo(name = "date") var date: Long,@ColumnInfo(name = "morning") var morning:Int,@ColumnInfo(name = "noon") var noon:Int,@ColumnInfo(name = "evening") var evening:Int,@ColumnInfo(name = "night") var night:Int,@ColumnInfo(name = "time_in_day") var time_in_day:Long,@ColumnInfo(name = "media_type") var media_type:String)