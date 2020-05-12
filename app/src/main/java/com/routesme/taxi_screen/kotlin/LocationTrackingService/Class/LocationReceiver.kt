package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.routesme.taxi_screen.kotlin.Class.App

class LocationReceiver(private val trackingDataLayer: TrackingDataLayer) : LocationListener {

    private var locationManager: LocationManager = App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun setUpLocationListener(): Boolean {
        return if (canGetLocation()) {
            if (isGPSEnabled())
                setLocationManagerProvider(LocationManager.GPS_PROVIDER)
            else if (isNetworkEnabled())
                setLocationManagerProvider(LocationManager.NETWORK_PROVIDER)

            true
        } else {
            false
        }
    }

    private fun setLocationManagerProvider(Provider: String) {
        if (Provider.isNotEmpty()) {
            try {
                locationManager.requestLocationUpdates(Provider, 1000L, 2F, this) //Location update with .. (Min Time = 1 second & Min Distance = 2 meters)
            } catch (ex: SecurityException) {
                Log.d("LocationManagerProvider", "Security Exception, no location available")
            }
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

    private fun canGetLocation() = isGPSEnabled() || isNetworkEnabled()

    private fun isGPSEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun isNetworkEnabled() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    //LocationListener Methods...
    override fun onLocationChanged(location: Location) {
        if (location != null) trackingDataLayer.insertLocation(location)
        //Toast.makeText(App.instance,"location changed ... lat:${location.latitude}, long:${location.longitude}",Toast.LENGTH_LONG).show()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(p0: String?) {}
    override fun onProviderDisabled(p0: String?) {}
}