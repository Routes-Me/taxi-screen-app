package com.routesme.vehicles.room.helper

import com.routesme.vehicles.room.AdvertisementDatabase
import com.routesme.vehicles.room.entity.AdvertisementTracking


class DatabaseHelperImpl(private val advertismentDatabase: AdvertisementDatabase) : DatabaseHelper {

    override suspend fun insertAdvertisement(advertisementTracking: AdvertisementTracking) = advertismentDatabase.advertisementTracking().insertAdvertisement(advertisementTracking)

    override suspend fun getItem(resourceNumber: String, timestamp: String): AdvertisementTracking = advertismentDatabase.advertisementTracking().getItem(resourceNumber, timestamp)

    override suspend fun updateSlotMorning(id: Int) {

        advertismentDatabase.advertisementTracking().updateSlotMorning(id)
    }

    override suspend fun updateSlotNoon(id: Int) {

        advertismentDatabase.advertisementTracking().updateSlotNoon(id)

    }

    override suspend fun updateSlotEvening(id: Int) {

        advertismentDatabase.advertisementTracking().updateSlotEvening(id)
    }

    override suspend fun updateSlotNight(id: Int) = advertismentDatabase.advertisementTracking().updateSlotNight(id)

    override suspend fun getLastItem(resourceNumber: String, timeStamp: String): AdvertisementTracking = advertismentDatabase.advertisementTracking().getLastItem(resourceNumber, timeStamp)

    override suspend fun getList(): List<AdvertisementTracking> = advertismentDatabase.advertisementTracking().getList()

    override suspend fun getAllList(): List<AdvertisementTracking> = advertismentDatabase.advertisementTracking().getAllList()

    override suspend fun deleteTable(id1: Int,id2:Int): Int = advertismentDatabase.advertisementTracking().deleteTable(id1,id2)

    override suspend fun deleteAllTable(): Int = advertismentDatabase.advertisementTracking().deleteAllTable()

}