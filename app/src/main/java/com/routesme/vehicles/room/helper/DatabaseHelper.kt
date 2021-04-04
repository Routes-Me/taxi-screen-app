package com.routesme.vehicles.room.helper

import com.routesme.vehicles.room.entity.AdvertisementTracking


interface DatabaseHelper {

    suspend fun insertAdvertisement(advertisementTracking: AdvertisementTracking)

    suspend fun getItem(id: String, timeStamp: Long): AdvertisementTracking

    suspend fun updateSlotMorning(id: Int)

    suspend fun updateSlotNoon(id: Int)

    suspend fun updateSlotEvening(id: Int)

    suspend fun updateSlotNight(id: Int)

    suspend fun getLastItem(id: String, timeStamp: Long): AdvertisementTracking

    suspend fun getList(timeStamp: Long): List<AdvertisementTracking>

    suspend fun getAllList(): List<AdvertisementTracking>

    suspend fun deleteTable(timeStamp: Long): Int

    suspend fun deleteAllTable(): Int

}