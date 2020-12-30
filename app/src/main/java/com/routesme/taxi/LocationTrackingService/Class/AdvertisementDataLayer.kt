package com.routesme.taxi.LocationTrackingService.Class

import android.util.Log
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Model.AdvertisementTracking
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.utils.Period

class AdvertisementDataLayer(){

    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val advertisement = trackingDatabase.advertisementTracking()
    private val MIN = 100000000

    fun insertOrUpdateRecords(id:Int,timeStamp:Long,period:Period){

        var analysisRecord = advertisement.getItem(id,timeStamp/MIN)

        if(analysisRecord!=null){

           update(analysisRecord.id,period)

        }else{

            advertisement.insertAdvertisement(AdvertisementTracking(advertisementId = id,date = timeStamp,morning = 0,noon = 0,evening = 0,night = 0,time_in_day = timeStamp/MIN))
            var lastItem = advertisement.getLastItem(id,timeStamp/MIN)
            update(lastItem.id,period)

        }

    }

    fun deleteData(currentDate: Long) = advertisement.deleteTable(currentDate/MIN)

    fun getList(currentDate:Long) = advertisement.getList(currentDate/MIN)

    fun update(id:Int,period:Period){

        when(period){

            Period.MORNING -> advertisement.updateSlotMorning(id)
            Period.NOON -> advertisement.updateSlotNoon(id)
            Period.EVENING -> advertisement.updateSlotEvening(id)
            Period.NIGHT -> advertisement.updateSlotNight(id)

        }
    }



}