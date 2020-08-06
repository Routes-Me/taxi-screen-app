package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi_screen.kotlin.Model.LocationJsonObject
import com.routesme.taxi_screen.kotlin.Model.VehicleLocation
import org.json.JSONException
import org.json.JSONObject
import tech.gusavila92.websocketclient.WebSocketClient
import java.text.SimpleDateFormat


class TrackingDataLayer(private var trackingWebSocket: WebSocketClient) {
    private val db = TrackingDatabase(App.instance).trackingDao()

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa")

    fun sendOfflineTrackingLocationsToServer() {
        if (!db.loadAllLocations().isNullOrEmpty()) {
                sendLocationViaSocket(db.loadAllLocations())
            clearTrackingTable()
        } else {
            Log.d("trackingWebSocketKotlin", "No offline location is exists!")
        }
    }

    fun insertLocation(location: Location) {
        val lastLocation = db.loadLastLocation()
        if (lastLocation == null || (location.latitude != lastLocation.latitude && location.longitude != lastLocation.longitude)){
            val currentLocation = VehicleLocation(0, location.latitude, location.longitude, System.currentTimeMillis()/1000)
            db.insertLocation(currentLocation)
            Log.d("trackingWebSocketKotlin", "Insert new location:  $currentLocation")
        }
    }

    fun locationChecker() {
        if (!db.loadAllLocations().isNullOrEmpty()) {
            val firstLocation = db.loadFirstLocation().location
            val lastLocation = db.loadLastLocation().location
            if (firstLocation != lastLocation) {
                if (distance(firstLocation, lastLocation) >= 2) {
                    val vehicleLocation= mutableListOf<VehicleLocation>().apply {
                        add(db.loadLastLocation())
                    }
                    sendLocationViaSocket(vehicleLocation)
                    clearTrackingTable()
                }
            }
        }
    }

    private fun distance(firstLocation: Location, lastLocation: Location) = firstLocation.distanceTo(lastLocation)

    private fun clearTrackingTable() {
        db.clearTrackingData()
        Log.d("trackingWebSocketKotlin", "Clear tracking table!")
    }

    private fun sendLocationViaSocket(vehicleLocations: List<VehicleLocation>){
        val locationJsonArray = JsonArray()
        for (l in vehicleLocations){
            val locationJsonObject: JsonObject = LocationJsonObject(l).toJSON()
            locationJsonArray.add(locationJsonObject)
        }

        val sendLocationObject = JsonObject()

        try { // Add the JSONArray to the JSONObject
            sendLocationObject.add("SendLocation", locationJsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.i("trackingWebSocket:  ", "Send-> $sendLocationObject")
        trackingWebSocket.send(sendLocationObject.toString())
    }
}