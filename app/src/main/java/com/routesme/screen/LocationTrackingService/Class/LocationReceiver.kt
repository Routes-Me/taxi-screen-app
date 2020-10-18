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

class LocationReceiver(private val trackingService: TrackingService, val hubConnection: HubConnection?) : LocationListener {
    private var dataLayer = TrackingDataLayer()
    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val minTime = 0L
    private val minDistance = 0F

    fun initializeLocationManager(){
        try {
            locationManager.requestLocationUpdates(locationProvider(), minTime, minDistance, this)
        } catch (ex: SecurityException) {
            Log.d("LocationManagerProvider", "Security Exception, no location available")
        }
    }

    private fun locationProvider(): String {
        return if (isGPSEnabled()) {
            LocationManager.GPS_PROVIDER
        }else {
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
                val feed = LocationFeed(latitude = it.latitude,longitude = it.longitude, timestamp = System.currentTimeMillis() / 1000)


                    val locationJsonArray = JsonArray()

                        val locationJsonObject: JsonObject = LocationJsonObject(feed).toJSON()
                        locationJsonArray.add(locationJsonObject)



                return getMessage(locationJsonArray.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null

       // return "{\"SendLocation\":[{\"latitude\":29.253058333333335,\"longitude\":47.94738166666666,\"timestamp\":\"1602665881\"},{\"latitude\":29.252981666666663,\"longitude\":47.947271666666666,\"timestamp\":\"1602665856\"},{\"latitude\":29.25293166666667,\"longitude\":47.947183333333335,\"timestamp\":\"1602665642\"},{\"latitude\":29.253011666666666,\"longitude\":47.94712,\"timestamp\":\"1602665615\"},{\"latitude\":29.253035,\"longitude\":47.94702333333333,\"timestamp\":\"1602665587\"},{\"latitude\":29.25312,\"longitude\":47.94692,\"timestamp\":\"1602665560\"},{\"latitude\":29.253220000000002,\"longitude\":47.94678333333333,\"timestamp\":\"1602665528\"},{\"latitude\":29.253418333333336,\"longitude\":47.946641666666665,\"timestamp\":\"1602665527\"},{\"latitude\":29.253596666666667,\"longitude\":47.94651,\"timestamp\":\"1602665524\"},{\"latitude\":29.25375166666667,\"longitude\":47.94636333333333,\"timestamp\":\"1602665508\"},{\"latitude\":29.25400833333333,\"longitude\":47.946303333333326,\"timestamp\":\"1602665507\"},{\"latitude\":29.254161666666665,\"longitude\":47.9462,\"timestamp\":\"1602665505\"},{\"latitude\":29.25435,\"longitude\":47.94610333333333,\"timestamp\":\"1602665501\"},{\"latitude\":29.254535,\"longitude\":47.94600666666667,\"timestamp\":\"1602665499\"},{\"latitude\":29.254689999999997,\"longitude\":47.94593833333333,\"timestamp\":\"1602665497\"},{\"latitude\":29.254835000000003,\"longitude\":47.945906666666666,\"timestamp\":\"1602665490\"},{\"latitude\":29.254985,\"longitude\":47.94584833333333,\"timestamp\":\"1602665488\"},{\"latitude\":29.255178333333333,\"longitude\":47.945769999999996,\"timestamp\":\"1602665487\"},{\"latitude\":29.255300000000002,\"longitude\":47.94569833333333,\"timestamp\":\"1602665485\"}]}"

    }

    override fun onLocationChanged(location: Location?) {


        location?.let { dataLayer.insertLocation(it) }
       // trackingService.sendMessage()
                dataLayer.getFeeds()?.let {
                     hubConnection?.let {
                         if (it.isConnected) it.invoke("SendLocation", it.toString())
                     }
                   // trackingService.sendMessage(it.toString())

            }

    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}

    //private fun getMessage(feeds:JSONArray) =  JSONObject().put("SendLocation", feeds).toString()

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