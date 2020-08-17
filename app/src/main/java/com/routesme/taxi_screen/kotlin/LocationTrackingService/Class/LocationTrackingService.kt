package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

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
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.Helper
import tech.gusavila92.websocketclient.WebSocketClient
import java.net.URI
import java.net.URISyntaxException

class LocationTrackingService(): Service() {

    private val sharedPreferences = App.instance.getSharedPreferences("userData", Activity.MODE_PRIVATE)
    private lateinit var authorityUrl: URI
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
    private var isWebSocketOpened = false

    private fun setTrackingWebSocketConfiguration(): WebSocketClient {
       // val  webSocketUrl = URI(Helper.getConfigValue("trackingWebSocketUrl"))
        val webSocket = object : WebSocketClient(getTrackingUrl()) {
            override fun onOpen() {
                isWebSocketOpened = true
                Log.d("Tracking-Logic", "WebSocket-onOpen: $isWebSocketOpened")
                /*
                Log.i("trackingWebSocket:  ", "Opened")
                sendDeviceIdToServer("deviceId:$tabletSerialNo")
                trackingDataLayer.sendOfflineTrackingLocationsToServer()
                handlerTracking?.post(runnableTracking)
                 */
            }
            override fun onTextReceived(message: String) {
                Log.d("Tracking-Logic", "WebSocket-onTextReceived: $message")
            }
            override fun onBinaryReceived(data: ByteArray) {}
            override fun onPingReceived(data: ByteArray) {}
            override fun onPongReceived(data: ByteArray) {}
            override fun onException(e: Exception) {
                isWebSocketOpened = false
                Log.d("Tracking-Logic", "WebSocket-onException: $isWebSocketOpened")
               // stopTrackingTimer()
            }
            override fun onCloseReceived() {
                isWebSocketOpened = false
                Log.d("Tracking-Logic", "WebSocket-onCloseReceived: $isWebSocketOpened")
                /*
                Log.i("trackingWebSocket:  ", "Closed !")
                stopTrackingTimer()
                 */
            }
        }
        webSocket.setConnectTimeout(10000)
        webSocket.setReadTimeout(60000)
        webSocket.enableAutomaticReconnection(10000)
        return webSocket
    }

    private fun getTrackingUrl(): URI {
        try {
            authorityUrl = URI(Helper.getConfigValue("trackingWebSocketAuthorityUrl"))
        }
        catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val builder: Uri.Builder = Uri.Builder()
        builder.scheme("http")
                .authority(authorityUrl.toString())
                .appendPath("trackServiceHub")
                .appendQueryParameter("vehicleId", getVehicleId())
                .appendQueryParameter("institutionId", getInstitutionId().toString())
                .appendQueryParameter("deviceId", getDeviceId())
        return URI(builder.build().toString())
    }

    private fun sendDeviceIdToServer(message: String) {
        trackingWebSocket.send(message)
        Log.i("trackingWebSocket:  ", "Send-> $message")
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
             if (!getVehicleId().isNullOrEmpty() && getInstitutionId() != 0 && !getDeviceId().isNullOrEmpty()) {
                 trackingWebSocket = setTrackingWebSocketConfiguration()
                 trackingDataLayer = TrackingDataLayer(trackingWebSocket)
                 locationReceiver = LocationReceiver(trackingDataLayer)
                 if (locationReceiver.setUpLocationListener()) {
                     trackingWebSocket.connect()
                     setupTrackingHandler()
                     handlerTracking?.post(runnableTracking)
                 }
             }else return
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

            if (isWebSocketOpened) trackingDataLayer.executeTrackingLogic()

            handlerTracking?.postDelayed(runnableTracking, 5000)
        }
        handlerTracking = Handler()

        /*
        runnableTracking = Runnable {
            isHandlerTrackingRunning = true
            Log.i("trackingWebSocket:  ", "Tracking Timer running ...")
            trackingDataLayer.locationChecker()
            handlerTracking?.postDelayed(runnableTracking, 5000)
        }
        handlerTracking = Handler()
        */
    }

    private fun stopTrackingTimer() {
        if (isHandlerTrackingRunning) {
            handlerTracking?.removeCallbacks(runnableTracking)
            isHandlerTrackingRunning = false
            Log.i("trackingWebSocketKotlin", "Tracking Timer stop ...")
        }
    }

    fun stopLocationTrackingService() {
        trackingWebSocket.onCloseReceived()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("trackingWebSocket:","onBind")
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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

    companion object {
        @get:Synchronized
        var instance:LocationTrackingService = LocationTrackingService()

        class LocationServiceBinder : Binder() {
            val service: LocationTrackingService
                get() = instance
        }
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val builder = Notification.Builder(applicationContext, "channel_01").setAutoCancel(true)
        return builder.build()
    }

    private fun getVehicleId() = sharedPreferences.getString("taxiPlateNumber", null)
    private fun getInstitutionId() = sharedPreferences.getInt("taxiOfficeId", 0)
    private fun getDeviceId() = sharedPreferences.getString("tabletSerialNo", null)
}