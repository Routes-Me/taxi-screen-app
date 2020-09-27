package com.routesme.taxi_screen.LocationTrackingService.Class

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.Class.Helper
import com.routesme.taxi_screen.Class.SharedPreference
import com.routesme.taxiscreen.R
import tech.gusavila92.websocketclient.WebSocketClient
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

class LocationTrackingService(): Service() {

    private lateinit var trackingWebSocket: WebSocketClient
    private lateinit var trackingDataLayer: TrackingDataLayer
    private lateinit var locationReceiver: LocationReceiver
    private var isHandlerTrackingRunning = false
    private var handlerTracking: Handler? = null
    private var runnableTracking: Runnable? = null
    private val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
    private var handlerCheckPermissions: Handler? = null
    private var runnableCheckPermissions: Runnable? = null
    private var permissionsHandlerRunning = false

    private val sharedPreferences = App.instance.getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
    private lateinit var authorityUrl: URI
    private var vehicleId: String? = null
    private var institutionId: String? = null
    private var deviceId: String? = null

    var isWebSocketAlive: Boolean = false
    companion object {
        @get:Synchronized
        var instance:LocationTrackingService = LocationTrackingService()

        class LocationServiceBinder : Binder() {
            val service: LocationTrackingService
                get() = instance
        }
    }

    private fun createWebSocket(): WebSocketClient {
        val webSocket = object : WebSocketClient(getTrackingUrl()) {
            override fun onOpen() {
                isWebSocketAlive = true
                Log.d("Tracking-Logic", "WebSocket-onOpen")
            }
            override fun onTextReceived(message: String) {
                Log.d("Tracking-Logic", "WebSocket-onTextReceived: $message")
            }
            override fun onBinaryReceived(data: ByteArray) {}
            override fun onPingReceived(data: ByteArray) {}
            override fun onPongReceived(data: ByteArray) {}
            override fun onException(e: Exception) {
                if (e is IOException) {
                    isWebSocketAlive = false
                }
                Log.d("Tracking-Logic", "WebSocket-onException: ${e.message}")
            }
            override fun onCloseReceived() {
                isWebSocketAlive = false
                Log.d("Tracking-Logic", "WebSocket-onCloseReceived")
            }
        }
        webSocket.setConnectTimeout(10000)
        webSocket.setReadTimeout(60000)
        webSocket.enableAutomaticReconnection(10000)
        return webSocket
    }

    private fun getTrackingUrl(): URI {
       // return URI(Helper.getConfigValue("trackingWebSocketUrl", R.raw.config))

        try {
           authorityUrl = URI(Helper.getConfigValue("trackingWebSocketAuthorityUrl",R.raw.config))
        }
       catch (e: URISyntaxException) {
            e.printStackTrace()
       }

        val builder: Uri.Builder = Uri.Builder()
       builder.scheme("ws")
               .encodedAuthority(authorityUrl.toString())
               .appendPath("trackServiceHub")
               .appendQueryParameter("vehicleId", vehicleId.toString())
               .appendQueryParameter("institutionId", institutionId.toString())
               .appendQueryParameter("deviceId", deviceId.toString())
       return URI(builder.build().toString())

    }

    fun checkPermissionsGranted(){
        if (hasPermissions(*permissions)){
            startTracking()
        }else{
            setupCheckPermissionsHandler()
            permissionsHandlerRunning = true
            handlerCheckPermissions?.postDelayed(runnableCheckPermissions,1000)

        }
    }
    private fun setupCheckPermissionsHandler() {
        Log.i("trackingWebSocket:","setupCheckPermissionsHandler")
        runnableCheckPermissions = Runnable {
            if (hasPermissions(*permissions) && permissionsHandlerRunning ) {permissionsHandlerRunning = false; handlerCheckPermissions?.removeCallbacks(runnableCheckPermissions); instance.startTracking() }
            Log.i("trackingWebSocket:","startCheckPermissionsHandler")
            handlerCheckPermissions?.postDelayed(runnableCheckPermissions, 1 * 60 * 1000)
        }
        handlerCheckPermissions = Handler()
    }
     private fun startTracking() {
         vehicleId = getVehicleId()
         institutionId = getInstitutionId()
         deviceId = getDeviceId()
        testingSetup()
             if (!vehicleId.isNullOrEmpty() && !institutionId.isNullOrEmpty() && !deviceId.isNullOrEmpty()) {
                 trackingWebSocket = createWebSocket()
                 trackingDataLayer = TrackingDataLayer(this, trackingWebSocket)
                 locationReceiver = LocationReceiver(trackingDataLayer)
                 if (locationReceiver.setUpLocationListener()) {
                     trackingWebSocket.connect()
                     setupTrackingHandler()
                     handlerTracking?.post(runnableTracking)
                 }
             }else return
    }

    private fun testingSetup() {
        vehicleId = "16"
        institutionId = "9"
        deviceId = "5"
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (p in permissions) {
                if (ContextCompat.checkSelfPermission(App.instance, p) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun setupTrackingHandler() {
        runnableTracking = Runnable {
            isHandlerTrackingRunning = true

            Log.i("trackingWebSocket:  ", "Tracking Timer running ...")
            if (isWebSocketAlive) trackingDataLayer.executeTrackingLogic()
            handlerTracking?.postDelayed(runnableTracking, 5000)
        }
        handlerTracking = Handler()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("trackingWebSocket:","onBind")
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i("trackingWebSocket:","onStartCommand")
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(12345678, getNotification())
        Log.i("trackingWebSocket:","onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("trackingWebSocket:","onDestroy")
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val builder = Notification.Builder(applicationContext, "channel_01").setAutoCancel(true)
        return builder.build()
    }

    private fun getVehicleId() = sharedPreferences.getString(SharedPreference.vehicle_id, null)
    private fun getInstitutionId() = sharedPreferences.getString(SharedPreference.institution_id, null)
    private fun getDeviceId() =  sharedPreferences.getString(SharedPreference.device_id, null)
}