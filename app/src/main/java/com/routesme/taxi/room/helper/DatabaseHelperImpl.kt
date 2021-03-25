package com.routesme.taxi.room.helper

import android.util.Log
import com.routesme.taxi.room.AdvertisementDatabase
import com.routesme.taxi.room.entity.AdvertisementTracking
import com.routesme.taxi.room.helper.DatabaseHelper


class DatabaseHelperImpl (private val advertismentDatabase: AdvertisementDatabase): DatabaseHelper {

    override suspend fun insertAdvertisement(advertisementTracking: AdvertisementTracking)  = advertismentDatabase.advertisementTracking().insertAdvertisement(advertisementTracking)

    override suspend fun getItem(id: String,timestamp:Long): AdvertisementTracking = advertismentDatabase.advertisementTracking().getItem(id,timestamp)

    override suspend fun updateSlotMorning(id: Int){

        advertismentDatabase.advertisementTracking().updateSlotMorning(id)
    }

    override suspend fun updateSlotNoon(id: Int) {

        advertismentDatabase.advertisementTracking().updateSlotNoon(id)

    }

    override suspend fun updateSlotEvening(id: Int) {

        advertismentDatabase.advertisementTracking().updateSlotEvening(id)
    }

    override suspend fun updateSlotNight(id: Int) = advertismentDatabase.advertisementTracking().updateSlotNight(id)

    override suspend fun getLastItem(id: String,timeStamp:Long): AdvertisementTracking = advertismentDatabase.advertisementTracking().getLastItem(id,timeStamp)

    override  fun getList(timeStamp:Long):List<AdvertisementTracking> = advertismentDatabase.advertisementTracking().getList(timeStamp)

    override suspend fun getAllList():List<AdvertisementTracking> = advertismentDatabase.advertisementTracking().getAllList()

    override  fun deleteTable(timeStamp:Long):Int = advertismentDatabase.advertisementTracking().deleteTable(timeStamp)

    override suspend fun deleteAllTable():Int = advertismentDatabase.advertisementTracking().deleteAllTable()

}