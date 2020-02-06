package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Database.AppDatabase
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Model.Tracking
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Model.TrackingLocation
import tech.gusavila92.websocketclient.WebSocketClient
import java.text.SimpleDateFormat
import java.util.*

class TrackingHandler(val context: Context, private var trackingWebSocket: WebSocketClient) {
    val db = AppDatabase(context).trackingDao()

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa")


    fun sendOfflineTrackingToServer() {
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
        val tracking = Tracking(0, trackingLocation, dateFormat.format(Date()))
        if (db.loadAllLocations().isNullOrEmpty()) {
            db.insertLocation(tracking)
            Log.d("trackingWebSocketKotlin", "Insert first location:  $tracking")
        } else {
            if (tracking.location != db.loadLastLocation().location) {
                db.insertLocation(tracking)
                Log.d("trackingWebSocketKotlin", "Insert new location:  $tracking")
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

    private fun distance(firstLocation: TrackingLocation, lastLocation: TrackingLocation): Float {
        val fLocation = Location("firstLocation")
        fLocation.latitude = firstLocation.latitude
        fLocation.longitude = firstLocation.longitude
        val lLocation = Location("lastLocation")
        lLocation.latitude = lastLocation.latitude
        lLocation.longitude = lastLocation.longitude

        return fLocation.distanceTo(lLocation)
    }

    private fun clearTrackingTable() {
        db.clearTrackingData()
        Log.d("trackingWebSocketKotlin", "Clear tracking table!")
    }

    private fun sendLocationViaSocket(t: Tracking) {
        val location = "location:${t.location.latitude},${t.location.longitude};timestamp:${t.timestamp}"
        trackingWebSocket.send(location)
        Log.d("trackingWebSocketKotlin", "Send location : $location")
    }
}