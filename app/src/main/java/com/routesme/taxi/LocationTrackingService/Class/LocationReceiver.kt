package com.routesme.taxi.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.LocationTrackingService.Model.LocationJsonObject
import com.routesme.taxi.uplevels.App
import org.json.JSONException
import org.json.JSONObject

class LocationReceiver() : LocationListener{
    private var dataLayer = TrackingDataLayer()
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val minTime = 5000L
    private val minDistance = 27F
    fun initializeLocationManager() {
        try {
            locationManager.requestLocationUpdates(minTime,minDistance,createFineCriteria(),this,null)
        } catch (ex: SecurityException) {
            Log.d("LocationManagerProvider", "Security Exception, no location available")
        }
    }

    fun isProviderEnabled() = isGPSEnabled() || isNetworkEnabled()
    private fun isGPSEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    private fun isNetworkEnabled() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    fun removeLocationUpdates() {
        locationManager.removeUpdates(this)
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocationMessage(): String? {
        locationManager.getLastKnownLocation(bestProvider)?.let {
            try {
                val feed = LocationFeed(latitude = it.latitude, longitude = it.longitude, timestamp = System.currentTimeMillis() / 1000)
                val locationJsonArray = JsonArray()
                val locationJsonObject: JsonObject = LocationJsonObject(feed).toJSON()
                locationJsonArray.add(locationJsonObject)
                return TrackingServiceHelper.instance.getMessage(locationJsonArray.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private val bestProvider = locationManager.getBestProvider(createFineCriteria(),true)

    override fun onLocationChanged(location: Location?) {
        location?.let { location ->
            dataLayer.insertLocation(location)
            Log.d("Test-location-service","Inserted location: $location")
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        val onStatusChangedMessage = "onStatusChanged ... provider: $p0, status: $p1, extras: $p2"
        Log.d("send-location-testing ",onStatusChangedMessage)
    }
    override fun onProviderEnabled(p0: String?) {
        val onProviderEnabledMessage = "onProviderEnabled ... Provider: $p0"
        Log.d("send-location-testing ",onProviderEnabledMessage)
    }
    override fun onProviderDisabled(p0: String?) {
        val onProviderDisabledMessage = "onProviderDisabled ... Provider: $p0"
        Log.d("send-location-testing ",onProviderDisabledMessage)
    }

    private fun createFineCriteria(): Criteria {
        return Criteria().apply {
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
}