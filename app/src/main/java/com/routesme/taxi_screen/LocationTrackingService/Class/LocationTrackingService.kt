package com.routesme.taxi_screen.LocationTrackingService.Class

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.Class.Helper
import com.routesme.taxi_screen.Class.SharedPreference
import com.routesme.taxi_screen.MVVM.View.HomeScreen.Activity.HomeActivity
import com.routesme.taxiscreen.R
import com.smartarmenia.dotnetcoresignalrclientjava.*
import tech.gusavila92.websocketclient.WebSocketClient
import java.lang.Exception
import java.net.URI
import java.net.URISyntaxException

class LocationTrackingService() : Service(), HubConnectionListener, HubEventListener {

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
   // var isWebSocketAlive: Boolean = false
    val token1 = "eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9lbWFpbGFkZHJlc3MiOiJ2dGhhcmFrYUByb3V0ZXNtZS5jb20iLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJzdXBlciIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvdXNlcmRhdGEiOiIzIiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvbmFtZWlkZW50aWZpZXIiOiIzIiwiZXhwIjoxNjAxNDc3NzI4LCJpc3MiOiJUcmFja1NlcnZpY2UiLCJhdWQiOiJUcmFja1NlcnZpY2UifQ.ML_5E-ztVECgrTtYbadyfpYsLe8lm0m-Y4MtDGRzfo4"

    companion object {
        @get:Synchronized
        var instance: LocationTrackingService = LocationTrackingService()

        class LocationServiceBinder : Binder() {
            val service: LocationTrackingService
                get() = instance
        }
    }
/*
    private fun signalRHubConnection(): HubConnection {
        val url = getTrackingUrl().toString()
        Log.d("SignalR-Url",url)
        val hubConnection = HubConnectionBuilder.create(url).build()
        hubConnection.on("SendLocation", { message: String? ->
            Log.d("SignalR", "send message: $message")
        }, String::class.java)
        return hubConnection
    }
    */

    private fun getSignalRHub(): HubConnection {
        val url = getTrackingUrl().toString()
        val authHeader = "Bearer $token1"
        val hubConnection = WebSocketHubConnectionP2(url, authHeader)

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
            hubConnection = getSignalRHub()
            hubConnection.addListener(this)
            hubConnection.subscribeToEvent("SendLocation", this)

            trackingDataLayer = TrackingDataLayer(this, hubConnection)
            locationReceiver = LocationReceiver(trackingDataLayer)
            if (locationReceiver.setUpLocationListener()) {
               // HubConnectionTask().execute(hubConnection)
                setupTrackingHandler()
                connect()
                //hubConnection.send("SendLocation", message)
                //trackingWebSocket.connect()

                //handlerTracking?.post(runnableTracking)
            }
        } else return
    }

    private fun testingSetup() {
        vehicleId = "16"
        institutionId = "5"
        deviceId = "1"
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
            trackingDataLayer.executeTrackingLogic()
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
/*
    internal class HubConnectionTask : AsyncTask<HubConnection?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        @SuppressLint("CheckResult")
        override fun doInBackground(vararg hubConnections: HubConnection?): Void? {
            val hubConnection = hubConnections[0]
            //hubConnection?.start()?.blockingAwait()
            hubConnection?.start()?.doOnComplete {
                if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
                    Log.d("SignalR", "Client connected successfully.")
                }
            }?.blockingAwait()

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
*/

    private fun connect() {
        try {
            hubConnection.connect()
        } catch (ex: Exception) {
            // runOnUiThread { Toast.makeText(this@MainActivity, ex.message, Toast.LENGTH_SHORT).show() }
            Log.d("SignalR","${ex.message} ,  ${ex}")
        }
    }

    override fun onConnected() {
        Log.d("SignalR", "onConnected")
        handlerTracking?.post(runnableTracking)
       // val message = "{ \"SendLocation\": [{ \"latitude\": \"80.200\", \"longitude\": \"80.200\", \"timestamp\": \"4756890945\"}] }"
       // connection.invoke("SendLocation", message)
    }

    override fun onMessage(message: HubMessage) {
        Log.d("SignalR", "onMessage: ${message.arguments}")
       // Toast.makeText(App.instance,"onMessage: ${message.target}\\n${Gson().toJson(message.arguments)}",Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(App.instance,"onMessage: ${message.target}\\n${Gson().toJson(message.arguments)}",Toast.LENGTH_LONG).show()
        }
    }

    override fun onDisconnected() {
        Log.d("SignalR", "onDisconnected")
        handlerTracking?.removeCallbacks(runnableTracking)
    }

    override fun onError(exception: Exception) {
        Log.d("SignalR", "onError: ${exception.message}")
    }

    override fun onEventMessage(message: HubMessage) {
        //  HomeActivity().runOnUiThread { Toast.makeText(this, "Event message: ${message.target}\n${Gson().toJson(message.arguments)}", Toast.LENGTH_SHORT).show() }
        Log.d("SignalR", "onEventMessage: ${message.target}\n" + "${Gson().toJson(message.arguments)}")
        //Toast.makeText(App.instance,"onEventMessage: ${message.target}\\n${Gson().toJson(message.arguments)}",Toast.LENGTH_LONG).show()
        /*
        Handler(Looper.getMainLooper()).post {
              Toast.makeText(this,"onEventMessage: ${message.target}\\n${Gson().toJson(message.arguments)}",Toast.LENGTH_LONG).show()
        }
        */
    }
}