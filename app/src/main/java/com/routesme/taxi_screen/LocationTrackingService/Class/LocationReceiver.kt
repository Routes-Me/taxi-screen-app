package com.routesme.taxi_screen.LocationTrackingService.Class

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.routesme.taxi_screen.Class.App
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocationReceiver(private val trackingDataLayer: TrackingDataLayer) : LocationListener {

    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var startLocation: Location? = null

    fun setUpLocationListener(): Boolean {
        return if (canGetLocation()) {
            if (isGPSEnabled()) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(App.instance, "GPS Enabled", Toast.LENGTH_SHORT).show()
                }
                setLocationManagerProvider(LocationManager.GPS_PROVIDER)
            }else if (isNetworkEnabled()) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(App.instance, "Network Enabled", Toast.LENGTH_SHORT).show()
                }
                setLocationManagerProvider(LocationManager.NETWORK_PROVIDER)
            }
            true
      } else {
           false
        }
    }

    private fun setLocationManagerProvider(provider: String) {
        if (provider.isNotEmpty()) {
            try {
                locationManager.requestLocationUpdates(provider, 1000L, 2.77F, this)
                startLocation = locationManager.getLastKnownLocation(provider)
            } catch (ex: SecurityException) {
                Log.d("LocationManagerProvider", "Security Exception, no location available")
            }
        }
    }

     fun getStartLocationMessage(): String? {
        if (startLocation == null){
            return null
        }else{
            val locationObject = JSONObject()
            val locationMessage = JSONObject()
            try {
                locationObject.put("latitude",startLocation?.latitude)
                locationObject.put("longitude",startLocation?.longitude)
                locationObject.put("timestamp",(System.currentTimeMillis()/1000).toString())
                startLocation = null
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val locationsArray = JSONArray().put(locationObject)
            try {
                locationMessage.put("SendLocation", locationsArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return locationMessage.toString()
        }
    }

    fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(App.instance)
                .setTitle("GPS settings")
                .setMessage("GPS is not enabled. Do you want to go to settings menu?")
                .setPositiveButton("Settings", DialogInterface.OnClickListener(function = positiveButtonClick))
                .setNegativeButton("Cancel", DialogInterface.OnClickListener(function = negativeButtonClick))
        alertDialog.show()
    }

    private val positiveButtonClick = { _: DialogInterface, which: Int -> App.instance.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
    private val negativeButtonClick = { dialog: DialogInterface, which: Int -> dialog.cancel() }

    private fun canGetLocation() =  isGPSEnabled() || isNetworkEnabled()

    private fun isGPSEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun isNetworkEnabled() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    //LocationListener Methods...
    override fun onLocationChanged(location: Location) {
        if (location != null) trackingDataLayer.insertLocation(location)
        Handler(Looper.getMainLooper()).post {
          Toast.makeText(App.instance,"location changed ... lat:${location.latitude}, long:${location.longitude}",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}
}