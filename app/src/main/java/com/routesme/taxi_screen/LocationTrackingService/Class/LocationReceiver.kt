package com.routesme.taxi_screen.LocationTrackingService.Class

import android.annotation.SuppressLint
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
    private var dataLayer: TrackingDataLayer? = null
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val minTime = 5000L
    private val minDistance = 27F

    private fun locationProvider(): String {
        return if (isGPSEnabled()) {
            LocationManager.GPS_PROVIDER
        }else {
            LocationManager.NETWORK_PROVIDER
        }
    }

    fun initializeLocationManager(){
        try {
            locationManager.requestLocationUpdates(locationProvider(), minTime, minDistance, this)
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

        locationManager.getLastKnownLocation(locationProvider())?.let {

            try {
                val feed = JSONObject()
                val message = JSONObject()

                feed.put("latitude", it.latitude)
                feed.put("longitude", it.longitude)
                feed.put("timestamp", (System.currentTimeMillis() / 1000).toString())

                message.put("SendLocation", JSONArray().put(feed))
                return message.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return null
    }

    override fun onLocationChanged(location: Location?) {
        dataLayer?.insertLocation(location)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}

    fun setDataLayer(trackingDataLayer: TrackingDataLayer) {
        this.dataLayer = trackingDataLayer
    }
}