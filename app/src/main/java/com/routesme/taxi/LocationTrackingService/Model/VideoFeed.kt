package com.routesme.taxi.LocationTrackingService.Model

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "VideoSaver")
class VideoFeed(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "file_path") var file_path: String, @ColumnInfo(name = "download_id") var download_id: Double, @ColumnInfo(name = "timestamp") var timestamp: Long) {

}