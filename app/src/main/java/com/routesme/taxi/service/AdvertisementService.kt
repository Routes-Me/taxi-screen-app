package com.routesme.taxi.service

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.LocationTrackingService.Class.AdvertisementDataLayer
import com.routesme.taxi.utils.Period
import com.routesme.taxi.utils.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class AdvertisementService : Service(){
    val advertisementDataLayer = AdvertisementDataLayer()
    override fun onBind(p0: Intent?): IBinder? {

        return null
    }

    companion object{
        var instance = AdvertisementService()
    }
    override fun onCreate() {
        super.onCreate()
        Log.d("Services","Service Created")
    }
    fun log(id:String, type:String){
        Log.d("Services","${Thread.currentThread().name} ${type}")

        advertisementDataLayer.insertOrUpdateRecords(id,DateHelper.instance.getCurrentDate(),DateHelper.instance.getCurrentPeriod(),type)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Services","Services Started ${Thread.currentThread().name}")
        return START_STICKY
    }
    override fun onDestroy() {
        Log.d("Services", "Services Destory")
        super.onDestroy()
    }

}