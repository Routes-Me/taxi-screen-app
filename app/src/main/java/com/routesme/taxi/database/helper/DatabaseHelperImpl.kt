package com.routesme.taxi.database.helper

import android.util.Log
import com.routesme.taxi.database.database.AdvertisementDatabase
import com.routesme.taxi.database.entity.AdvertisementTracking


class DatabaseHelperImpl (private val advertismentDatabase: AdvertisementDatabase):DatabaseHelper{

    override suspend fun insertAdvertisement(advertisementTracking: AdvertisementTracking)  = advertismentDatabase.advertisementTracking().insertAdvertisement(advertisementTracking)

    override suspend fun getItem(id: String,timestamp:Long): AdvertisementTracking = advertismentDatabase.advertisementTracking().getItem(id,timestamp)

    override suspend fun updateSlotMorning(id: Int){
        Log.d("Thread Helper","${Thread.currentThread().name}, ${Thread.currentThread().id}")
        advertismentDatabase.advertisementTracking().updateSlotMorning(id)
    }

    override suspend fun updateSlotNoon(id: Int) {

        Log.d("Thread Helper","${Thread.currentThread().name}, ${Thread.currentThread().id}")
        advertismentDatabase.advertisementTracking().updateSlotNoon(id)

    }

    override suspend fun updateSlotEvening(id: Int) {

        advertismentDatabase.advertisementTracking().updateSlotEvening(id)
    }

    override suspend fun updateSlotNight(id: Int) = advertismentDatabase.advertisementTracking().updateSlotNight(id)

    override suspend fun getLastItem(id: String,timeStamp:Long):AdvertisementTracking = advertismentDatabase.advertisementTracking().getLastItem(id,timeStamp)

    override suspend fun getList(timeStamp:Long):List<AdvertisementTracking> = advertismentDatabase.advertisementTracking().getList(timeStamp)

    override suspend fun getAllList():List<AdvertisementTracking> = advertismentDatabase.advertisementTracking().getAllList()

    override suspend fun deleteTable(timeStamp:Long):Int = advertismentDatabase.advertisementTracking().deleteTable(timeStamp)

    override suspend fun deleteAllTable():Int = advertismentDatabase.advertisementTracking().deleteAllTable()

}