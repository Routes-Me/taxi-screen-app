package com.routesme.vehicles.service.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import com.routesme.vehicles.App
import com.routesme.vehicles.room.entity.LocationCoordinate
import com.routesme.vehicles.room.entity.LocationFeed
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

class LocationReceiver : LocationListener {
    private var locationManagerThread: HandlerThread? = null
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var isLocationUpdatesRequested = false
    private val minTime = TimeUnit.MINUTES.toMillis(1)
    private val minDistance = 27F
    var currentLocationCoordinate = LocationCoordinate(0.00, 0.00, 0.0F, 0.0F, 0L)

    companion object{
        @get:Synchronized
        var instance = LocationReceiver()
    }

    fun startLocationUpdatesListener() {
        try {
            locationManagerThread = HandlerThread("LocationManagerThread").apply {
                start()
                locationManager.requestLocationUpdates(minTime, minDistance, createFineCriteria(), instance, this.looper)
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
            locationManager.removeUpdates(instance)
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
            val locationFeed = LocationFeed(latitude = it.latitude, longitude = it.longitude, bearing = it.bearing, bearingAccuracyDegrees = it.bearingAccuracyDegrees, timestamp = System.currentTimeMillis() / 1000)
            currentLocationCoordinate = locationFeed.coordinate
            return locationFeed
        }
        return null
    }

    private val bestProvider: String = locationManager.getBestProvider(createFineCriteria(), true).toString()

    override fun onLocationChanged(location: Location) {
        location?.let {
            val locationFeed = LocationFeed(latitude = it.latitude, longitude = it.longitude, bearing = it.bearing, bearingAccuracyDegrees = it.bearingAccuracyDegrees, timestamp = System.currentTimeMillis() / 1000)
            currentLocationCoordinate = locationFeed.coordinate
            Log.d("LocationArchiving", "onLocationChanged")
            EventBus.getDefault().post(locationFeed)
        }
    }

    override fun onStatusChanged(p0: String, p1: Int, p2: Bundle) {}
    override fun onProviderEnabled(p0: String) {}
    override fun onProviderDisabled(p0: String) {}
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