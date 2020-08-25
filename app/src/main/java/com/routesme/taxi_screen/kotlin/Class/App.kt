package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.gms.nearby.messages.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Class.LocationTrackingService
import com.routesme.taxi_screen.kotlin.Model.AuthCredentials
import com.routesme.taxi_screen.kotlin.Model.PaymentData
import com.routesme.taxi_screen.kotlin.View.PaymentScreen.Activity.PaymentScreen
import com.routesme.taxiscreen.R
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {
    private val displayManager = DisplayManager.instance
    private var proxy: HttpProxyCacheServer? = null
    var authCredentials: AuthCredentials? = null
    var isNewLogin = false
    var institutionId = -999
    var taxiPlateNumber: String? = null
    var vehicleId: Int = -999
    var institutionName: String? = null
    private var trackingService: LocationTrackingService? = null
    private lateinit var telephonyManager: TelephonyManager
    private  var paymentData = PaymentData()
    private val operations = Operations.instance
    private var receivedSuccessfullyMessage: String = ""
    private var minute = 60 * 1000



    companion object {

        @get:Synchronized
        var instance = App()
        val imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)
        //video player...
        var simpleCache: SimpleCache? = null
        var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor? = null
        var exoDatabaseProvider: ExoDatabaseProvider? = null
        var exoPlayerCacheSize: Long = 90 * 1024 * 1024
        //val firebaseAnalytics = FirebaseAnalytics.getInstance(App.instance)
        lateinit var currentActivity:Context

        val nearbySubscribeOptions: SubscribeOptions = SubscribeOptions.Builder()
                .setStrategy(nearbyStrategy())
                .build()


        val nearbyPublishOptions: PublishOptions = PublishOptions.Builder()
                .setStrategy(nearbyStrategy())
                .build()

        private fun nearbyStrategy(): Strategy {
            return Strategy.Builder()
                    .setTtlSeconds(Strategy.TTL_SECONDS_MAX)
                   // .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
                    .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        logApplicationStartingPeriod(currentPeriod())
        displayManager.setAlarm(this)
        operations.context = this

        //video player...
        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = ExoDatabaseProvider(this)
        }

        if (simpleCache == null) {
            simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
        }

        val intent = Intent(instance, LocationTrackingService::class.java)
        this.startService(intent)
        //this.getApplication().startForegroundService(intent);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("LocationTrackingService")) {
                Log.i("trackingWebSocket:", "onServiceConnected")
                trackingService = (service as LocationTrackingService.Companion.LocationServiceBinder).service
                LocationTrackingService.instance.checkPermissionsGranted()

            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "LocationTrackingService") {
                trackingService = null
                Log.i("trackingWebSocket:", "onServiceDisconnected")
            }
        }
    }


    private fun logApplicationStartingPeriod(timePeriod: TimePeriod) {
        val params = Bundle()
        params.putString("TimePeriod", timePeriod.toString())
        FirebaseAnalytics.getInstance(this).logEvent("application_starting_period", params)
    }
    private fun currentPeriod(): TimePeriod {
        return if (currentDate().after(parseDate("04:00")) && currentDate().before(parseDate("12:00"))) TimePeriod.Morning
        else if (currentDate().after(parseDate("12:00")) && currentDate().before(parseDate("17:00"))) TimePeriod.Noon
        else if (currentDate().after(parseDate("17:00")) && currentDate().before(parseDate("24:00"))) TimePeriod.Evening
        else TimePeriod.Night
    }
    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return parseDate("$hour:$minute")
    }
    @SuppressLint("SimpleDateFormat")
    private fun parseDate(time: String) = SimpleDateFormat("HH:mm").parse(time)
    enum class TimePeriod { Morning, Noon, Evening, Night }


    //Payment Service...
    private val messageListener = object : MessageListener() {
        override fun onFound(paymentMessage: Message) {
            // Toast.makeText(this@HomeScreen,"message: ${String(paymentMessage.content)}",Toast.LENGTH_SHORT).show()

            val dataArray = String(paymentMessage.content).split(",").toTypedArray()

            paymentData.driverToken = dataArray[0]
            paymentData.paymentAmount = dataArray[1].toDouble()

            sendReceivedSuccessfullyMessage()
            passPaymentData()

        }
        override fun onLost(message: Message?) {}
    }

    private fun sendReceivedSuccessfullyMessage() {
        receivedSuccessfullyMessage = "${getString(R.string.received)},${paymentData.paymentAmount}"
       // operations.publish(receivedSuccessfullyMessage)
    }

    /*
    override fun onStop() {
        if (receivedSuccessfullyMessage.isNotEmpty()) operations.unPublish(receivedSuccessfullyMessage)
        Nearby.getMessagesClient(this).unsubscribe(messageListener)
        super.onStop()
    }
*/
    private fun passPaymentData(){
       // hideFragment(PaymentFragment.instance)
        //paymentData.apply { driverToken = "9347349"; paymentAmount = 1.500 }  //for test only
      //  val bundle = Bundle().apply {putSerializable("paymentData", paymentData)}
      //  val paymentFragment = PaymentFragment.instance.apply { arguments =bundle }
        startActivity(Intent(this, PaymentScreen::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("paymentData",paymentData))

        val runnableBanner = Runnable {
            PaymentScreen().finish()
        }
        val handlerBanner = Handler()
        handlerBanner.postDelayed(runnableBanner, (30 * 1000).toLong())

        //supportFragmentManager.beginTransaction().replace(R.id.paymentFragment_container, paymentFragment).commit()

    }

    /*
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) show(fragment)
            else add(R.id.payment_container, fragment)
            supportFragmentManager.fragments.forEach { if (it != fragment && it.isAdded) hide(it) }
        }.commit()
    }

    private fun hideFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded && fragment.isVisible) hide(fragment)
        }.commit()
    }
*/
}