package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import com.routesme.taxi_screen.kotlin.Class.Helper
import tech.gusavila92.websocketclient.WebSocketClient
import java.net.URI

class LocationTrackingService(val context: Context) {

    private lateinit var trackingWebSocket: WebSocketClient
    private lateinit var trackingDataLayer: TrackingDataLayer
    private var isHandlerTrackingRunning = false
    private var handlerTracking: Handler? = null
    private var runnableTracking: Runnable? = null

    init {
        val tabletSerialNo = context.getSharedPreferences("userData", Activity.MODE_PRIVATE).getString("tabletSerialNo", null);
        if (!tabletSerialNo.isNullOrEmpty()) {
            trackingWebSocket = setTrackingWebSocketConfiguration(trackingWebSocketUri(), tabletSerialNo)
            trackingDataLayer = TrackingDataLayer(context, trackingWebSocket)
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
        val locationReceiver = LocationReceiver(context, trackingDataLayer)
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
}