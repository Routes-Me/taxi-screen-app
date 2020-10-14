package com.routesme.taxi_screen.LocationTrackingService.Class

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.routesme.taxi_screen.uplevels.App
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocationReceiver() : LocationListener {
    private var trackingDataLayer: TrackingDataLayer? = null
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var lastKnownLocation: Location? = null
    private val minTime = 0L//5000L
    private val minDistance = 0F//27F

    fun initializeLocationManager(){
            if (isGPSEnabled()) {
                setLocationManagerProvider(LocationManager.GPS_PROVIDER)
            } else {
                setLocationManagerProvider(LocationManager.NETWORK_PROVIDER)
            }
    }
    private fun setLocationManagerProvider(provider: String) {
        try {
            lastKnownLocation = locationManager.getLastKnownLocation(provider)
            locationManager.requestLocationUpdates(provider, minTime, minDistance, this)
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

    fun getLastKnownLocationMessage(): String? {
        if (lastKnownLocation == null) {
            return null
        } else {
            val locationObject = JSONObject()
            val locationMessage = JSONObject()
            try {
                locationObject.put("latitude", lastKnownLocation?.latitude)
                locationObject.put("longitude", lastKnownLocation?.longitude)
                locationObject.put("timestamp", (System.currentTimeMillis() / 1000).toString())
                lastKnownLocation = null
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val locationsArray = JSONArray().put(locationObject)
            try {
                locationMessage.put("SendLocation", locationsArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return locationMessage.toString()
        }
    }

    //LocationListener Methods...
    override fun onLocationChanged(location: Location?) {
        trackingDataLayer?.insertLocation(location)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}

    fun setDataLayer(trackingDataLayer: TrackingDataLayer) {
        this.trackingDataLayer = trackingDataLayer
    }
}