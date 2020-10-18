package com.routesme.screen.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.screen.LocationTrackingService.Model.LocationFeed
import com.routesme.screen.LocationTrackingService.Model.LocationJsonObject
import com.routesme.screen.uplevels.App
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocationReceiver(private val trackingService: TrackingService, private val hubConnection: HubConnection?) : LocationListener {
    private var dataLayer = TrackingDataLayer()
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val minTime =      0L //5000L
    private val minDistance =  0F //27F

    fun initializeLocationManager() {
        try {
            locationManager.requestLocationUpdates(locationProvider(), minTime, minDistance, this)
        } catch (ex: SecurityException) {
            Log.d("LocationManagerProvider", "Security Exception, no location available")
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
        location?.let { dataLayer.insertLocation(it) }
        dataLayer.getFeeds().let {
            getMessage(getFeedsJsonArray(it).toString())?.let { it1 -> trackingService.sendMessage(it1) }
            dataLayer.deleteFeeds(it.last().id)
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}

    private fun getFeedsJsonArray(feeds: List<LocationFeed>): JsonArray? {
        //val feeds = locationFeedsDao.getResults()
        return if (!feeds.isNullOrEmpty()){
            getJsonArray(feeds)
        }else{
            null
        }
    }

    private fun getJsonArray(locationFeeds: List<LocationFeed>): JsonArray {
        val locationJsonArray = JsonArray()

        for (l in locationFeeds){
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

}