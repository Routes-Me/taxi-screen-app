package com.routesme.vehicles.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.routesme.vehicles.room.entity.AdvertisementTracking


@Dao
interface AdvertisementDoa {

    @Insert
    suspend fun insertAdvertisement(advertisement: AdvertisementTracking)

    @Transaction
    @Query("SELECT id, advertisementId, resourceNumber, date,morning,noon,evening,night,time_in_day,media_type  FROM tbl_advertisement_tracking WHERE resourceNumber = :resourceNumber AND time_in_day = :timestamp")
    suspend fun getItem(resourceNumber: String, timestamp: String): AdvertisementTracking

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET morning = morning+1 WHERE id = :id ")
    suspend fun updateSlotMorning(id: Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET noon = noon+1 WHERE id = :id ")
    suspend fun updateSlotNoon(id: Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET evening = evening+1 WHERE id = :id ")
    suspend fun updateSlotEvening(id: Int)

    @Transaction
    @Query("UPDATE tbl_advertisement_tracking SET night = night+1 WHERE id = :id ")
    suspend fun updateSlotNight(id: Int)

    @Transaction
    @Query("SELECT * FROM tbl_advertisement_tracking WHERE resourceNumber = :resourceNumber AND time_in_day = :timestamp")
    suspend fun getLastItem(resourceNumber: String, timestamp: String): AdvertisementTracking

    @Transaction
    @Query("SELECT id,advertisementId, resourceNumber, date,morning,noon,evening,night,time_in_day,media_type FROM tbl_advertisement_tracking ORDER BY date ASC")
    suspend fun getList(): List<AdvertisementTracking>

    @Transaction
    @Query("SELECT id,advertisementId, resourceNumber, date,morning,noon,evening,night,time_in_day,media_type FROM tbl_advertisement_tracking  ORDER BY date ASC")
    suspend fun getAllList(): List<AdvertisementTracking>

    /*@Query("DELETE FROM tbl_advertisement_tracking WHERE time_in_day != :currentDate")
    suspend fun deleteTable(currentDate: String): Int*/

    @Query("DELETE FROM tbl_advertisement_tracking WHERE id BETWEEN :id1 AND :id2")
    suspend fun deleteTable(id1: Int,id2:Int): Int

    @Query("DELETE FROM tbl_advertisement_tracking")
    suspend fun deleteAllTable(): Int


}