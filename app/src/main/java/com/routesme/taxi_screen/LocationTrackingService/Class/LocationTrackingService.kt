package com.routesme.taxi_screen.LocationTrackingService.Class

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.core.content.ContextCompat
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.Class.Helper
import com.routesme.taxi_screen.Class.SharedPreference
import com.routesme.taxiscreen.R
import tech.gusavila92.websocketclient.WebSocketClient
import java.net.URI
import java.net.URISyntaxException


class LocationTrackingService() : Service() {

    private lateinit var trackingWebSocket: WebSocketClient
    private lateinit var hubConnection: HubConnection
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
    private val NOTIFICATION_ID = 12345678

    var isWebSocketAlive: Boolean = false

    companion object {
        @get:Synchronized
        var instance: LocationTrackingService = LocationTrackingService()

        class LocationServiceBinder : Binder() {
            val service: LocationTrackingService
                get() = instance
        }
    }

    private fun signalRHubConnection(): HubConnection {
        val url = getTrackingUrl().toString()
        val hubConnection = HubConnectionBuilder.create(url).build()
        hubConnection.on("Send", { message: String? ->
            Log.d("SignalR", "send message: $message")
        }, String::class.java)
        return hubConnection
    }

    private fun getTrackingUrl(): URI {
        // return URI("ws://vmtprojectstage.uaenorth.cloudapp.azure.com:5002/trackServiceHub?vehicleId=16&institutionId=9&deviceId=5")
        // return URI(Helper.getConfigValue("trackingWebSocketUrl", R.raw.config))

        try {
            authorityUrl = URI(Helper.getConfigValue("trackingWebSocketAuthorityUrl", R.raw.config))
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val builder: Uri.Builder = Uri.Builder()
        builder.scheme("http")
                .encodedAuthority(authorityUrl.toString())
                .appendPath("trackServiceHub")
                .appendQueryParameter("vehicleId", vehicleId.toString())
                .appendQueryParameter("institutionId", institutionId.toString())
                .appendQueryParameter("deviceId", deviceId.toString())
        return URI(builder.build().toString())
    }

    fun checkPermissionsGranted() {
        if (hasPermissions(*permissions)) {
            startTracking()
        } else {
            setupCheckPermissionsHandler()
            permissionsHandlerRunning = true
            handlerCheckPermissions?.postDelayed(runnableCheckPermissions, 1000)

        }
    }

    private fun setupCheckPermissionsHandler() {
        Log.i("trackingWebSocket:", "setupCheckPermissionsHandler")
        runnableCheckPermissions = Runnable {
            if (hasPermissions(*permissions) && permissionsHandlerRunning) {
                permissionsHandlerRunning = false; handlerCheckPermissions?.removeCallbacks(runnableCheckPermissions); instance.startTracking()
            }
            Log.i("trackingWebSocket:", "startCheckPermissionsHandler")
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
            // trackingWebSocket = createWebSocket()
            hubConnection = signalRHubConnection()
            trackingDataLayer = TrackingDataLayer(this, hubConnection)
            locationReceiver = LocationReceiver(trackingDataLayer)
            if (locationReceiver.setUpLocationListener()) {
                HubConnectionTask().execute(hubConnection)

                //trackingWebSocket.connect()
                setupTrackingHandler()
                handlerTracking?.post(runnableTracking)
            }
        } else return
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
        Log.i("trackingWebSocket:", "onBind")
        return LocationServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i("trackingWebSocket:", "onStartCommand")
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, getNotification())
        Log.i("trackingWebSocket:", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("trackingWebSocket:", "onDestroy")
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
    private fun getDeviceId() = sharedPreferences.getString(SharedPreference.device_id, null)

    internal class HubConnectionTask : AsyncTask<HubConnection?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        @SuppressLint("CheckResult")
        override fun doInBackground(vararg hubConnections: HubConnection?): Void? {
            val hubConnection = hubConnections[0]
            //hubConnection?.start()?.blockingAwait()
            hubConnection?.start()?.doOnComplete { Log.d("SignalR", "Client connected successfully.") }?.blockingAwait()

            /*
            hubConnection!!.start()
                    .subscribeOn(Schedulers.single())
                    .doOnComplete {
                        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                            Log.d("SignalR", "start: " + "${hubConnection.getConnectionState()}")
                            hubConnection.send("Send", "welcome")
                        }
                    }
                    .blockingAwait();

*/

            return null
        }
    }
}