package com.routesme.taxi.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi.LocationTrackingService.Model.LocationJsonObject
import com.routesme.taxi.uplevels.App
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocationReceiver(private val hubConnection: HubConnection?) : LocationListener{
    private var dataLayer = TrackingDataLayer()
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var isConnected = false
    private val minTime = 5000L
    private val minDistance = 27F
    private val mLocationRequest: LocationRequest? = null
    private val mLastLocation: Location? = null
    private val isFreshLocation = true
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
                return getMessage(locationJsonArray.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }

    override fun onLocationChanged(location: Location?) {
        val provider = if (isGPSEnabled()) "GPS_PROVIDER" else if (isNetworkEnabled()) "NETWORK_PROVIDER" else "No Provider"
        val messageOnLocationChanged = "onLocationChanged: $location, Accuracy: ${location?.accuracy}, Provider: ${location?.provider},,,  Enabled Provider: $provider"
        Log.d("send-location-testing",messageOnLocationChanged)
        location?.let { location ->
            dataLayer.insertLocation(location)
            //val messageInsert = "Insert location into DB: $location"
            if (isConnected) {
                dataLayer.getFeeds().let {
                    getMessage(getFeedsJsonArray(it).toString())?.let { it1 ->
                        hubConnection?.invoke("SendLocation", it1)
                        //val messageSend = "Send locations from DB: $it1"
                        dataLayer.deleteFeeds(it.first().id, it.last().id)
                    }
                }
            }
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

    private fun getFeedsJsonArray(feeds: List<LocationFeed>): JsonArray? {
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

    private val bestProvider = locationManager.getBestProvider(createFineCriteria(),true)

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