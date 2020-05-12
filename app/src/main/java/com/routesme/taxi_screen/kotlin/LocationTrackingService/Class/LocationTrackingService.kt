package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.Helper
import tech.gusavila92.websocketclient.WebSocketClient
import java.net.URI

class LocationTrackingService(): Service() {

    private lateinit var trackingWebSocket: WebSocketClient
    private lateinit var trackingDataLayer: TrackingDataLayer
    private var isHandlerTrackingRunning = false
    private var handlerTracking: Handler? = null
    private var runnableTracking: Runnable? = null

    init {
        val tabletSerialNo = App.instance.getSharedPreferences("userData", Activity.MODE_PRIVATE).getString("tabletSerialNo", null);
        if (!tabletSerialNo.isNullOrEmpty()) {
            trackingWebSocket = setTrackingWebSocketConfiguration(trackingWebSocketUri(), tabletSerialNo)
            trackingDataLayer = TrackingDataLayer(trackingWebSocket)
            startTracking()
        }
    }

    private fun trackingWebSocketUri()= URI(Helper.getConfigValue("trackingWebSocketUri"))

    private fun setTrackingWebSocketConfiguration(trackingUri: URI, tabletSerialNo: String): WebSocketClient {
        val webSocket = object : WebSocketClient(trackingUri) {
            override fun onOpen() {
                Log.i("trackingWebSocket:  ", "Opened")
                sendDeviceIdToServer("deviceId:$tabletSerialNo")
                trackingDataLayer.sendOfflineTrackingLocationsToServer()
                handlerTracking?.post(runnableTracking)
            }
            override fun onTextReceived(message: String) {
                Log.i("trackingWebSocket:  ", "Received : $message")
            }
            override fun onBinaryReceived(data: ByteArray) {}
            override fun onPingReceived(data: ByteArray) {}
            override fun onPongReceived(data: ByteArray) {}
            override fun onException(e: Exception) {
                stopTrackingTimer()
            }

            override fun onCloseReceived() {
                Log.i("trackingWebSocket:  ", "Closed !")
                stopTrackingTimer()
            }
        }
        webSocket.setConnectTimeout(10000)
        webSocket.setReadTimeout(60000)
        webSocket.enableAutomaticReconnection(5000)
        return webSocket
    }

    private fun sendDeviceIdToServer(message: String) {
        trackingWebSocket.send(message)
        Log.i("trackingWebSocket:  ", "Send message:  $message")
    }

    private fun startTracking() {
        val locationReceiver = LocationReceiver(trackingDataLayer)
        if (locationReceiver.setUpLocationListener()) {
            setupTrackingTimer()
            trackingWebSocket.connect()
        } else {
            locationReceiver.showAlertDialog()
        }
    }

    private fun setupTrackingTimer() {
        runnableTracking = Runnable {
            isHandlerTrackingRunning = true
            Log.i("trackingWebSocket:  ", "Tracking Timer running ...")
            trackingDataLayer.locationChecker()
            handlerTracking?.postDelayed(runnableTracking, 5000)
        }
        handlerTracking = Handler()
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
        return START_NOT_STICKY
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

}