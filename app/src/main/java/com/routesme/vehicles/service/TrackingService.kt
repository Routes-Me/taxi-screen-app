package com.routesme.vehicles.service

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.routesme.vehicles.service.receiver.LocationReceiver
import com.routesme.vehicles.room.dao.LocationFeedsDao
import com.routesme.vehicles.room.TrackingDatabase
import com.routesme.vehicles.room.entity.LocationFeed
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.uplevels.Account
import com.routesme.vehicles.App
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.room.entity.LocationCoordinate
import com.routesme.vehicles.uplevels.ActivatedBusInfo
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class TrackingService : Service() {

    private var activatedBusInfo: ActivatedBusInfo? = null
    private val thisApiCorService by lazy { RestApiService.createNewCorService(this, true) }

    private lateinit var hubConnection: HubConnection
    private lateinit var locationReceiver: LocationReceiver
    private lateinit var db: TrackingDatabase
    private lateinit var locationFeedsDao: LocationFeedsDao
    private var sendFeedsTimer: Timer? = null
    private val thisApiCoreService by lazy { RestApiService.createOldCorService(this) }
    private var vehicleId: String? = null

    override fun onCreate() {
        super.onCreate()
        activatedBusInfo = ActivatedBusInfo()
        vehicleId = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE).getString(SharedPreferencesHelper.vehicle_id, null)
        hubConnection = prepareHubConnection()
        EventBus.getDefault().register(this)
        locationReceiver = LocationReceiver()
        db = TrackingDatabase(App.instance)
        locationFeedsDao = db.locationFeedsDao()
        //insertTestFeeds()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        locationReceiver.stopLocationUpdatesListener()
        EventBus.getDefault().unregister(this)
        sendFeedsTimer?.cancel()
        db.apply { if (isOpen) close() }
        hubConnection.apply { if (this.connectionState == HubConnectionState.CONNECTED) stop() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(ServiceInfo.Tracking.serviceId, getNotification())
        locationReceiver.apply {
            if (isProviderEnabled()) {
                startLocationUpdatesListener()
                startHubConnection()
            }
            vehicleId?.let { scheduleLocationFeeds() }
        }
        return START_STICKY
    }

    private fun getNotification(): Notification {
        val channel = NotificationChannel(ServiceInfo.Tracking.channelId, ServiceInfo.Tracking.channelName, NotificationManager.IMPORTANCE_NONE)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        return Notification.Builder(this, ServiceInfo.Tracking.channelId).setSmallIcon(R.mipmap.routes_icon_light).setAutoCancel(true).build()
    }

    private fun insertTestFeeds() {
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 1..100000) {
                val locationFeed = LocationFeed(latitude = 28.313749, longitude = 48.0342295, timestamp = System.currentTimeMillis() / 1000)
                locationFeedsDao.insertLocation(locationFeed)
            }
        }
    }

    private fun scheduleLocationFeeds() {
        sendFeedsTimer = Timer("SendFeedsTimer", true).apply {
            schedule(TimeUnit.SECONDS.toMillis(0), TimeUnit.MINUTES.toMillis(5)) {
                GlobalScope.launch(Dispatchers.IO) {
                    locationFeedsDao.getFeeds().let { feeds ->
                        Log.d("LocationArchiving", "PostCoordinates... Get feeds from Room DB: $feeds")
                        if (!feeds.isNullOrEmpty()) {
                            sendFeeds(feeds)
                        }
                    }
                }
            }
        }
    }

    private fun sendFeeds(feeds: List<LocationFeed>) {
        val coordinates = feeds.map { it.coordinate }
        val call = thisApiCoreService.locationCoordinates(vehicleId!!, coordinates)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    GlobalScope.launch(Dispatchers.IO) {
                        locationFeedsDao.deleteFeeds(feeds.first().id, feeds.last().id)
                        Log.d("LocationArchiving", "PostCoordinates... Successfully . Deleted feeds: ${feeds.first().id} - ${feeds.last().id}")
                    }
                }
            }
            override fun onFailure(call: Call<String>, throwable: Throwable) {}
        })
    }

    private fun prepareHubConnection(): HubConnection {
        val trackingUrl = getTrackingUrl().toString()
        Log.d("URL", "${trackingUrl}")
        return HubConnectionBuilder
                .create(trackingUrl)
                .withHeader("Authorization", Account().accessToken)
                .build().apply {
                    serverTimeout = TimeUnit.MINUTES.toMillis(6)
                    onClosed {
                        Log.d("SocketSrv", "onClosed, Exception: $it")
                        startHubConnection()
                    }
                    on("CommonMessage", { message: String? ->
                        message?.let {
                            Log.d("SocketSrv", "CommonMessage .. Message: $it")
                        }
                    }, String::class.java)
                }
    }

    private fun getTrackingUrl(): Uri {
        val trackingAuthorityUrl = URI(BuildConfig.STAGING_TRACKING_WEBSOCKET_AUTHORITY_URL).toString()
        val sharedPref = applicationContext.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        val vehicleId = sharedPref.getString(SharedPreferencesHelper.vehicle_id, null)
        val institutionId = sharedPref.getString(SharedPreferencesHelper.institution_id, null)
        val deviceId = sharedPref.getString(SharedPreferencesHelper.device_id, null)
        Log.d("SocketSrv", "DeviceId: $deviceId")
        return Uri.Builder().apply {
            scheme("https")
            encodedAuthority(trackingAuthorityUrl)
            appendPath("trackServiceHub")
            appendQueryParameter("vehicleId", vehicleId)
            appendQueryParameter("institutionId", institutionId)
            appendQueryParameter("deviceId", deviceId)
        }.build()
    }

    private fun startHubConnection() {
        hubConnection.start()
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(@NonNull d: Disposable) {}
                    override fun onError(@NonNull e: Throwable) {
                        Log.d("SocketSrv", "onError, Throwable: $e")
                        Timer("signalRReconnection", true).apply {
                            schedule(TimeUnit.MINUTES.toMillis(1)) {
                                startHubConnection()
                            }
                        }
                    }

                    override fun onComplete() {
                        Log.d("SocketSrv", "onComplete")
                        locationReceiver.getLastKnownLocationMessage()?.let {
                            val feedCoordinates = mutableListOf<LocationFeed>().apply { add(it) }.map { it.coordinate }
                            hubConnection.let { if (it.connectionState == HubConnectionState.CONNECTED) it.send("SendLocations", feedCoordinates) }
                            if (BuildConfig.FLAVOR == "bus") {sendBusLocation(feedCoordinates.last())}
                        }
                    }
                })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(locationFeed: LocationFeed) {
        Log.d("LocationArchiving", "EventBus... TrackingService... received LocationFeed: $locationFeed")
        GlobalScope.launch(Dispatchers.IO) {
            hubConnection.let {
                if (it.connectionState == HubConnectionState.CONNECTED) {
                    val feeds = mutableListOf<LocationFeed>().apply { add(locationFeed) }.toList()
                    val feedCoordinates = feeds.map { it.coordinate }
                    if (it.connectionState == HubConnectionState.CONNECTED) it.send("SendLocations", feedCoordinates)
                    if (BuildConfig.FLAVOR == "bus") {sendBusLocation(feedCoordinates.last())}
                    Log.d("LocationArchiving", "hubConnection connected... sent LocationFeed by signalR")
                } else {
                    locationFeedsDao.insertLocation(locationFeed)
                    Log.d("LocationArchiving", "hubConnection not connected... inserted LocationFeed into room DB")
                }
            }
        }
    }

    private fun sendBusLocation(locationCoordinate: LocationCoordinate) {
        activatedBusInfo?.let {
            val busLiveTrackingCredentials = BusLiveTrackingCredentials(BusID = it.busId,Latitude = locationCoordinate.latitude, Longitude = locationCoordinate.longitude)
            Log.d("BusLiveTracking", "busLiveTrackingCredentials: $busLiveTrackingCredentials")

            val call = thisApiCorService.sendBusLocation(busLiveTrackingCredentials)
            call.enqueue(object : Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    if (response.isSuccessful && response.body() != null) {
                        val busLiveTrackingDTO = Gson().fromJson<BusLiveTrackingDTO>(response.body(), BusLiveTrackingDTO::class.java)
                        Log.d("BusLiveTracking", "Successful... BusLiveTrackingDTO:${busLiveTrackingDTO.copy()}")
                    } else {
                        Log.d("BusLiveTracking", "Failed... Code : ${response.code()} ")
                        if (response.errorBody() != null && response.code() == HttpURLConnection.HTTP_CONFLICT) {
                            val objError = JSONObject(response.errorBody()!!.string())
                            val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        } else {
                            val error = Error(detail = response.message(), statusCode = response.code())
                            val errors = mutableListOf<Error>().apply { add(error) }.toList()
                            val responseErrors = ResponseErrors(errors)
                        }
                    }
                }
                override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                    Log.d("BusLiveTracking", "onFailure... Throwable: $throwable")
                }
            })
        }
    }
}