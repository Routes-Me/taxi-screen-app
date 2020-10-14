package com.routesme.taxi_screen.LocationTrackingService.Class

import android.location.Location
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi_screen.uplevels.App
import com.routesme.taxi_screen.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi_screen.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi_screen.LocationTrackingService.Model.LocationJsonObject
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TrackingDataLayer(private var hubConnection: HubConnection?) {
    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val locationFeedsDao = trackingDatabase.locationFeedsDao()

    private fun getMessage(result: String): String {
        val messageObject = JSONObject()
        val feedsArray = JSONArray(result)
        try {
            messageObject.put("SendLocation", feedsArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return messageObject.toString()
    }

    fun insertLocation(currentLocation: Location?) {
        if (currentLocation != null){
            val previousLocation = locationFeedsDao.loadLastLocation()
            if (previousLocation != null){
                if (currentLocation.latitude != previousLocation.latitude && currentLocation.longitude != previousLocation.longitude){
                   addNewLocation(currentLocation)
                }
            }else{
                addNewLocation(currentLocation)
            }
        }
    }

    private fun ainsertLocation(location: Location){
        locationFeedsDao.insertLocation(getLocationFeed(location))

    }

    private fun addNewLocation(currentLocation: Location) {
        locationFeedsDao.insertLocation(getLocationFeed(currentLocation))
        hubConnection?.let  {

            if (it.isConnected){
                getLocationsResult()?.let {
                    sendMessage(it)
                }
            }
        }
    }

    private fun getLocationFeed(location: Location) = LocationFeed(latitude =  location.latitude, longitude = location.longitude, timestamp = System.currentTimeMillis()/1000)

    private fun getLocationsResult(): String? {
        val result = locationFeedsDao.getResults()
        return if (!result.isNullOrEmpty()){
            getJsonArray(result).toString()
        }else{
            null
        }
    }

    private fun getJsonArray(locationFeeds: List<LocationFeed>):JsonArray {
        val locationJsonArray = JsonArray()
        for (l in locationFeeds){
            val locationJsonObject: JsonObject = LocationJsonObject(l).toJSON()
            locationJsonArray.add(locationJsonObject)
        }
        return locationJsonArray
    }

    private fun sendMessage(result: String){
        val message = getMessage(result)
        Log.d("SendLocation-Message",message)
        hubConnection?.invoke("SendLocation", message)
    }
}