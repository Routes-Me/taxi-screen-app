package com.routesme.taxi_screen.LocationTrackingService.Class

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.SharedPreferences
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
import com.routesme.taxiscreen.R
import com.smartarmenia.dotnetcoresignalrclientjava.*
import java.net.URI
import java.net.URISyntaxException

class LocationTrackingService() : Service(), HubConnectionListener, HubEventListener {

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
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authorityUrl: URI
    private var vehicleId: String? = null
    private var institutionId: String? = null
    private var deviceId: String? = null
    private var token: String? = null
    private val NOTIFICATION_ID = 12345678

    private val reconnectionHandler = Handler(Looper.getMainLooper())
    private var reconnectionRunnable: Runnable? = null

    companion object {
        @get:Synchronized
        var instance: LocationTrackingService = LocationTrackingService()
        //  val token = instance.getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE).getString(SharedPreference.token, null)

        class LocationServiceBinder : Binder() {
            val service: LocationTrackingService
                get() = instance
        }
    }

    private fun getSignalRHub(): HubConnection {
        val url = getTrackingUrl().toString()
        val authHeader = "Bearer $token"
        val hubConnection = WebSocketHubConnectionP2(url, authHeader)

        return hubConnection
    }

    private fun getTrackingUrl(): URI {

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
        sharedPreferences = App.instance.getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
        vehicleId = getVehicleId()
        institutionId = getInstitutionId()
        deviceId = getDeviceId()
        token = getToken()
        // testingSetup()
        if (!vehicleId.isNullOrEmpty() && !institutionId.isNullOrEmpty() && !deviceId.isNullOrEmpty() && !token.isNullOrEmpty()) {
            hubConnection = getSignalRHub()
            hubConnection.addListener(this)
            hubConnection.subscribeToEvent("SendLocation", this)

            trackingDataLayer = TrackingDataLayer(hubConnection)
            locationReceiver = LocationReceiver(trackingDataLayer)
            if (locationReceiver.setUpLocationListener()) {
                setupTrackingHandler()
                connect()
            }
        } else return
    }

    private fun testingSetup() {
        vehicleId = "17"
        institutionId = "5"
        deviceId = "2"
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
        // sharedPreferences.getString(SharedPreference.vehicle_id, null)//.getString(SharedPreference.token, null)

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
    private fun getToken() = sharedPreferences.getString(SharedPreference.token, null)
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
            Log.d("SignalR", "${ex.message} ,  ${ex}")
        }
    }

    override fun onConnected() {
        val startLocationMessage = locationReceiver.getStartLocationMessage()
        if (!startLocationMessage.isNullOrEmpty()) hubConnection.invoke("SendLocation", startLocationMessage)
        Log.d("SignalR", "onConnected")
        handlerTracking?.post(runnableTracking)
       // handlerTracking?.removeCallbacks(this)
    }

    override fun onMessage(message: HubMessage) {
        Log.d("SignalR", "onMessage: ${message.arguments}")
        // Toast.makeText(App.instance,"onMessage: ${message.target}\\n${Gson().toJson(message.arguments)}",Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(App.instance, "onMessage: ${message.target}\\n${Gson().toJson(message.arguments)}", Toast.LENGTH_LONG).show()
        }
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

    override fun onDisconnected() {
        Log.d("SignalR", "onDisconnected")
        handlerTracking?.removeCallbacks(runnableTracking)

        Handler(Looper.getMainLooper()).postDelayed({
            connect()
        }, 2 * 60 * 1000)
    }

    override fun onError(exception: Exception) {
        Log.d("SignalR", "onError: ${exception.message}")
        handlerTracking?.removeCallbacks(runnableTracking)
        reconnectionHandler.postDelayed({
            connect()
        }, 1 * 60 * 1000)
    }
}