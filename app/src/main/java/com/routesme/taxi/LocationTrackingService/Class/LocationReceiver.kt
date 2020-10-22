package com.routesme.taxi.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.LocationTrackingService.Model.LocationJsonObject
import com.routesme.taxi.uplevels.App
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocationReceiver(private val hubConnection: HubConnection?) : LocationListener {
    private var dataLayer = TrackingDataLayer()
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var isConnected = false

    private val minTime = 5000L
    private val minDistance = 27F
    private lateinit var criteria :Criteria

    fun initializeLocationManager() {
        try {
            criteria = getCriteria()
           // locationManager.requestLocationUpdates(locationProvider(), minTime, minDistance, this)

            locationManager.requestLocationUpdates(minTime, minDistance,Criteria(),this, Looper.getMainLooper())
            
        } catch (ex: SecurityException) {
            Log.d("LocationManagerProvider", "Security Exception, no location available")
        }
    }

    private fun getCriteria(): Criteria{
        return Criteria().apply {
            accuracy = Criteria.ACCURACY_COARSE
            powerRequirement = Criteria.POWER_HIGH
            isAltitudeRequired = false
            isSpeedRequired = true
            isCostAllowed = true
            isBearingRequired = false
            horizontalAccuracy = Criteria.ACCURACY_HIGH
            verticalAccuracy = Criteria.ACCURACY_HIGH
        }
    }

    private fun locationProvider(): String {
        return if (isGPSEnabled()) {
            LocationManager.GPS_PROVIDER
        } else {
            LocationManager.NETWORK_PROVIDER
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
                Log.d("send-location-testing","LastKnownLocation: lat: ${it.latitude}, long: ${it.longitude} ")
                val feed = LocationFeed(latitude = it.latitude, longitude = it.longitude, timestamp = System.currentTimeMillis() / 1000)
                val locationJsonArray = JsonArray()
                val locationJsonObject: JsonObject = LocationJsonObject(feed).toJSON()
                locationJsonArray.add(locationJsonObject)
                return getMessage(locationJsonArray.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }

    override fun onLocationChanged(location: Location?) {
        Log.d("send-location-testing","onLocationChanged: $location")
        location?.let { location ->
            dataLayer.insertLocation(location)
            Log.d("send-location-testing","Insert location into DB: $location")
            if (isConnected) {
                dataLayer.getFeeds().let {
                    getMessage(getFeedsJsonArray(it).toString())?.let { it1 ->
                        hubConnection?.invoke("SendLocation", it1)
                        Log.d("send-location-testing","Send locations from DB: $location")
                        dataLayer.deleteFeeds(it.first().id, it.last().id)
                    }
                }
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}

    private fun getFeedsJsonArray(feeds: List<LocationFeed>): JsonArray? {
        //val feeds = locationFeedsDao.getResults()
        return if (!feeds.isNullOrEmpty()) {
            getJsonArray(feeds)
        } else {
            null
        }
    }

    private fun getJsonArray(locationFeeds: List<LocationFeed>): JsonArray {
        val locationJsonArray = JsonArray()

        for (l in locationFeeds) {
            val locationJsonObject: JsonObject = LocationJsonObject(l).toJSON()
            locationJsonArray.add(locationJsonObject)
        }
        return locationJsonArray
    }

    private fun getMessage(messageFeeds: String): String? {
        val messageObject = JSONObject()
        val feedsArray = JSONArray(messageFeeds)
        try {
            messageObject.put("SendLocation", feedsArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return messageObject.toString()
    }

    fun isHubConnected(isConnected: Boolean) {
        this.isConnected = isConnected
    }
}