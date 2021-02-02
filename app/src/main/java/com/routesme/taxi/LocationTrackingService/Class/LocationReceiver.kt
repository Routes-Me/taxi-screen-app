package com.routesme.taxi.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.uplevels.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationReceiver() : LocationListener{
    private var locationManagerThread: HandlerThread? = null
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var isLocationUpdatesRequested = false
    private val minTime = 5000L
    private val minDistance = 27F
    private val db = TrackingDatabase(App.instance)
    private val locationFeedsDao = db.locationFeedsDao()
    fun startLocationUpdatesListener() {
        try {
            locationManagerThread = HandlerThread("LocationManagerThread").apply {
                start()
                locationManager.requestLocationUpdates(minTime,minDistance,createFineCriteria(),this@LocationReceiver,this.looper)
                isLocationUpdatesRequested = true
            }
        } catch (ex: SecurityException) {
            Log.d("LocationManagerProvider", "Security Exception, no location available")
        }
    }

    fun isProviderEnabled() = isGPSEnabled() || isNetworkEnabled()
    private fun isGPSEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    private fun isNetworkEnabled() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    fun stopLocationUpdatesListener() {
        if (isLocationUpdatesRequested) {
            locationManager.removeUpdates(this@LocationReceiver)
            isLocationUpdatesRequested = false
        }

        if (locationManagerThread != null) {
            if (locationManagerThread!!.isAlive) locationManagerThread!!.quitSafely()
            locationManagerThread = null
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocationMessage(): LocationFeed? {
        locationManager.getLastKnownLocation(bestProvider)?.let {
           return LocationFeed(latitude = it.latitude, longitude = it.longitude, timestamp = System.currentTimeMillis() / 1000)
        }
        return null
    }

    private val bestProvider = locationManager.getBestProvider(createFineCriteria(),true)

    override fun onLocationChanged(location: Location?) {
        Log.d("LocationReceiverThread","onLocationChanged... ${Thread.currentThread().name}")
        location?.let {
            GlobalScope.launch(Dispatchers.IO) {
              //  Log.d("GlobalScope-Thread","Insert: ${Thread.currentThread().name}")
                val locationFeed = LocationFeed(latitude = it.latitude, longitude = it.longitude, timestamp = System.currentTimeMillis() / 1000)
                locationFeedsDao.insertLocation(locationFeed)
            }
            Log.d("Test-location-service","Inserted location: $it")
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}

    private fun createFineCriteria() = Criteria().apply {
            accuracy = Criteria.ACCURACY_FINE
            isAltitudeRequired = false
            isBearingRequired = false
            isSpeedRequired = true
            isCostAllowed = true
            powerRequirement = Criteria.POWER_HIGH
            horizontalAccuracy = Criteria.ACCURACY_HIGH
            verticalAccuracy = Criteria.ACCURACY_HIGH
        }
}