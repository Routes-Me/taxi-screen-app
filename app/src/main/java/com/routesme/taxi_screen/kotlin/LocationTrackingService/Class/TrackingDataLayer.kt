package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi_screen.kotlin.Model.VehicleLocation
import com.routesme.taxi_screen.kotlin.Model.TrackingLocation
import tech.gusavila92.websocketclient.WebSocketClient
import java.text.SimpleDateFormat
import java.util.*

class TrackingDataLayer(val context: Context, private var trackingWebSocket: WebSocketClient) {
    private val db = TrackingDatabase(context).trackingDao()

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa")

    fun sendOfflineTrackingLocationsToServer() {
        if (!db.loadAllLocations().isNullOrEmpty()) {
            for (t in db.loadAllLocations()) {
                sendLocationViaSocket(t)
            }
            clearTrackingTable()
        } else {
            Log.d("trackingWebSocketKotlin", "No offline location is exists!")
        }
    }

    fun insertLocation(trackingLocation: TrackingLocation) {
        val currentLocation = VehicleLocation(0, trackingLocation, dateFormat.format(Date()))
        if (db.loadAllLocations().isNullOrEmpty()) {
            db.insertLocation(currentLocation)
            Log.d("trackingWebSocketKotlin", "Insert first location:  $currentLocation")
        } else {
            if (currentLocation.location != db.loadLastLocation().location) {
                db.insertLocation(currentLocation)
                Log.d("trackingWebSocketKotlin", "Insert new location:  $currentLocation")
            }
        }
    }

    fun locationChecker() {
        if (!db.loadAllLocations().isNullOrEmpty()) {
            val firstLocation = db.loadFirstLocation().location
            val lastLocation = db.loadLastLocation().location
            if (firstLocation != lastLocation) {
                if (distance(firstLocation, lastLocation) >= 2) {
                    sendLocationViaSocket(db.loadLastLocation())
                    clearTrackingTable()
                }
            }
        }
    }

    private fun distance(firstLocation: TrackingLocation, lastLocation: TrackingLocation) = convertLocation(firstLocation,"firstLocation").distanceTo(convertLocation(lastLocation,"lastLocation"))


    private fun convertLocation(trackingLocation: TrackingLocation, providerName:String):Location{
        val location = Location(providerName)
        location.latitude = trackingLocation.latitude
        location.longitude = trackingLocation.longitude
        return location
    }

    private fun clearTrackingTable() {
        db.clearTrackingData()
        Log.d("trackingWebSocketKotlin", "Clear tracking table!")
    }

    private fun sendLocationViaSocket(vehicleLocation: VehicleLocation) {
        val location = "location:${vehicleLocation.location.latitude},${vehicleLocation.location.longitude};timestamp:${vehicleLocation.timestamp}"
        trackingWebSocket.send(location)
        Log.d("trackingWebSocketKotlin", "Send location : $location")
    }
}