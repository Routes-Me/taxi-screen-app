package com.routesme.taxi.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
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
    private val locationProvide:LocationProvider?=null
    var mLastLocation: Location? = null
    private val minTime = 5000L
    private val minDistance = 100F


    fun initializeLocationManager() {
        try {
            Log.d("send-location-testing ","Best Provider: ${bestProvider.name}")
            locationManager.requestLocationUpdates(bestProvider.name,minTime,minDistance,this)

        } catch (ex: SecurityException) {
            Log.d("LocationManagerProvider", "Security Exception, no location available")
        }
    }
/*
    private val locationProvider: LocationProvider = locationManager.getProvider(locationManager.getBestProvider(getHighCriteria(),true))

    private fun getHighCriteria():Criteria{
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        criteria.powerRequirement = Criteria.POWER_HIGH
        criteria.isAltitudeRequired = false
        criteria.isSpeedRequired = true
        criteria.isCostAllowed = true
        criteria.isBearingRequired = false
        criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
        criteria.verticalAccuracy = Criteria.ACCURACY_HIGH
        return criteria
    }

    /*
    fun initializeLocationManager() {
        try {
            locationManager.requestLocationUpdates(locationProvider(),minTime,minDistance,this)
            criteria.apply {
                accuracy = Criteria.ACCURACY_COARSE
                powerRequirement = Criteria.POWER_HIGH
                isAltitudeRequired = false
                isSpeedRequired = true
                isCostAllowed = true
                isBearingRequired = false
                horizontalAccuracy = Criteria.ACCURACY_HIGH
                verticalAccuracy = Criteria.ACCURACY_HIGH
            }
        } catch (ex: SecurityException) {
            Log.d("LocationManagerProvider", "Security Exception, no location available")
        }
    }
*/*/
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
        locationManager.getLastKnownLocation(bestProvider.name)?.let {
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
                        Log.d("send-location-testing","Send locations from DB: $it1")
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

    // get high accuracy provider
    private val bestProvider = locationManager.getProvider(locationManager.getBestProvider(createFineCriteria(),true))

    /** this criteria needs high accuracy, high power, and cost  */
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