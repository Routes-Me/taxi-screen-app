package com.routesme.taxi.LocationTrackingService.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.routesme.taxi.LocationTrackingService.Model.AdvertisementTracking
import java.sql.Timestamp

@Dao
interface  AdvertisementDoa {

    @Insert
    fun insertAdvertisement(advertisement: AdvertisementTracking)

    @Transaction
    @Query("SELECT id,advertisementId, date,morning,noon,evening,night,time_in_day,media_type  FROM tbl_advertisement_tracking WHERE advertisementId = :id AND time_in_day = :timestamp")
    fun getItem(id:Int,timestamp: Long):AdvertisementTracking?

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET morning = morning+1 WHERE id = :id ")
    fun updateSlotMorning(id:Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET noon = noon+1 WHERE id = :id ")
    fun updateSlotNoon(id:Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET evening = evening+1 WHERE id = :id ")
    fun updateSlotEvening(id:Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET night = night+1 WHERE id = :id ")
    fun updateSlotNight(id:Int)

    @Transaction
    @Query("SELECT * FROM tbl_advertisement_tracking WHERE advertisementId = :id AND time_in_day = :timestamp")
    fun getLastItem(id:Int,timestamp: Long):AdvertisementTracking

    @Transaction
    @Query("SELECT id,advertisementId, date,morning,noon,evening,night,time_in_day,media_type FROM tbl_advertisement_tracking WHERE time_in_day != :currentDate  ORDER BY date ASC" )
    fun getList(currentDate:Long):List<AdvertisementTracking>

    @Transaction
    @Query("SELECT id,advertisementId, date,morning,noon,evening,night,time_in_day,media_type FROM tbl_advertisement_tracking  ORDER BY date ASC" )
    fun getAllList():List<AdvertisementTracking>

    @Query("DELETE FROM tbl_advertisement_tracking WHERE time_in_day != :currentDate")
    fun deleteTable(currentDate: Long): Int

    @Query("DELETE FROM tbl_advertisement_tracking")
    fun deleteAllTable(): Int


}