package com.routesme.taxi.LocationTrackingService.Class

import android.util.Log
import com.routesme.taxi.LocationTrackingService.Database.AdvertisementDatabase
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Model.AdvertisementTracking
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.utils.Period

class AdvertisementDataLayer(){

    private val trackingDatabase = AdvertisementDatabase.invoke(App.instance)
    private val advertisement = trackingDatabase.advertisementTracking()
    private val MIN = 100000000

    fun insertOrUpdateRecords(id:String,timeStamp:Long,period:Period,type:String){

        var analysisRecord = advertisement.getItem(id,timeStamp/MIN)

        if(analysisRecord!=null){

           update(analysisRecord.id,period)

        }else{

            advertisement.insertAdvertisement(AdvertisementTracking(advertisementId = id,date = timeStamp,morning = 0,noon = 0,evening = 0,night = 0,time_in_day = timeStamp/MIN,media_type =type ))
            var lastItem = advertisement.getLastItem(id,timeStamp/MIN)
            update(lastItem.id,period)

        }

    }

    fun deleteData(currentDate: Long) = advertisement.deleteTable(currentDate/MIN)

    fun deleteAllData() = advertisement.deleteAllTable()

    fun getList(currentDate:Long) = advertisement.getList(currentDate/MIN)

    fun getAllList() = advertisement.getAllList()

    fun update(id:Int,period:Period){

        when(period){

            Period.MORNING -> advertisement.updateSlotMorning(id)
            Period.NOON -> advertisement.updateSlotNoon(id)
            Period.EVENING -> advertisement.updateSlotEvening(id)
            Period.NIGHT -> advertisement.updateSlotNight(id)

        }
    }



}