package com.routesme.vehicles.room.helper

import com.routesme.vehicles.room.entity.AdvertisementTracking


interface DatabaseHelper {

    suspend fun insertAdvertisement(advertisementTracking: AdvertisementTracking)

    suspend fun getItem(resourceNumber: String, timeStamp: String): AdvertisementTracking

    suspend fun updateSlotMorning(id: Int)

    suspend fun updateSlotNoon(id: Int)

    suspend fun updateSlotEvening(id: Int)

    suspend fun updateSlotNight(id: Int)

    suspend fun getLastItem(resourceNumber: String, timeStamp: String): AdvertisementTracking

    suspend fun getList(timeStamp: String): List<AdvertisementTracking>

    suspend fun getAllList(): List<AdvertisementTracking>

    suspend fun deleteTable(timeStamp: String): Int

    suspend fun deleteAllTable(): Int

}