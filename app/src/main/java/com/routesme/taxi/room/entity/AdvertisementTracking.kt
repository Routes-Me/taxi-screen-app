package com.routesme.taxi.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_advertisement_tracking")
class AdvertisementTracking(@PrimaryKey(autoGenerate = true) var id:Int = 0, @ColumnInfo(name = "advertisementId") var advertisementId: String, @ColumnInfo(name = "date") var date: Long, @ColumnInfo(name = "morning") var morning:Int, @ColumnInfo(name = "noon") var noon:Int, @ColumnInfo(name = "evening") var evening:Int, @ColumnInfo(name = "night") var night:Int, @ColumnInfo(name = "time_in_day") var time_in_day:Long, @ColumnInfo(name = "media_type") var media_type:String)