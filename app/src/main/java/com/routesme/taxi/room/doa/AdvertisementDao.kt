package com.routesme.taxi.room.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.routesme.taxi.room.entity.AdvertisementTracking


@Dao
interface  AdvertisementDoa {

    @Insert
    suspend fun insertAdvertisement(advertisement: AdvertisementTracking)

    @Transaction
    @Query("SELECT id,advertisementId, date,morning,noon,evening,night,time_in_day,media_type  FROM tbl_advertisement_tracking WHERE advertisementId = :id AND time_in_day = :timestamp")
    suspend fun getItem(id:String,timestamp: Long): AdvertisementTracking

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET morning = morning+1 WHERE id = :id ")
    suspend fun updateSlotMorning(id:Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET noon = noon+1 WHERE id = :id ")
    suspend fun updateSlotNoon(id:Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET evening = evening+1 WHERE id = :id ")
    suspend fun updateSlotEvening(id:Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET night = night+1 WHERE id = :id ")
    suspend fun updateSlotNight(id:Int)

    @Transaction
    @Query("SELECT * FROM tbl_advertisement_tracking WHERE advertisementId = :id AND time_in_day = :timestamp")
    suspend fun getLastItem(id:String,timestamp: Long): AdvertisementTracking

    @Transaction
    @Query("SELECT id,advertisementId, date,morning,noon,evening,night,time_in_day,media_type FROM tbl_advertisement_tracking WHERE time_in_day != :currentDate  ORDER BY date ASC" )
    suspend fun getList(currentDate:Long):List<AdvertisementTracking>

    @Transaction
    @Query("SELECT id,advertisementId, date,morning,noon,evening,night,time_in_day,media_type FROM tbl_advertisement_tracking  ORDER BY date DESC" )
    suspend fun getAllList():List<AdvertisementTracking>

    @Query("DELETE FROM tbl_advertisement_tracking WHERE time_in_day != :currentDate")
    suspend fun deleteTable(currentDate: Long): Int

    @Query("DELETE FROM tbl_advertisement_tracking")
    suspend fun deleteAllTable(): Int


}