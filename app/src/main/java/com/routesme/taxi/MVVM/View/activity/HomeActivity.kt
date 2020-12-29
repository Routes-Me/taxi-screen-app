package com.routesme.taxi.MVVM.View.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.BuildConfig
import com.routesme.taxi.Class.DisplayManager
import com.routesme.taxi.Class.HomeScreenHelper
import com.routesme.taxi.Class.ScreenBrightness
import com.routesme.taxi.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi.LocationTrackingService.Class.AdvertisementDataLayer
import com.routesme.taxi.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi.LocationTrackingService.Model.AdvertisementTracking
import com.routesme.taxi.MVVM.Model.IModeChanging
import com.routesme.taxi.MVVM.Model.ReportResponse
import com.routesme.taxi.MVVM.Model.SubmitApplicationVersionCredentials
import com.routesme.taxi.MVVM.Model.SubmitApplicationVersionResponse
import com.routesme.taxi.MVVM.View.fragment.ContentFragment
import com.routesme.taxi.MVVM.View.fragment.SideMenuFragment
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.ViewModel.SubmitApplicationVersionViewModel
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.R
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.uplevels.App
import kotlinx.android.synthetic.main.home_screen.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class HomeActivity : PermissionsActivity(), IModeChanging {
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private val helper = HomeScreenHelper(this)
    private var isHotspotAlive = false
    private var pressedTime: Long = 0
    private lateinit var mView: View
    private var clickTimes = 0
    private var sideMenuFragment: SideMenuFragment? = null
    private var player : SimpleExoPlayer?=null
    private  var from_date:String?=null
    private  var deviceId:String?=null
    private val advertisementTracking = AdvertisementDataLayer()
    private var getList:List<AdvertisementTracking>?=null

    private val connectivityManager by lazy { getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DisplayManager.instance.registerActivity(this)
        if (DisplayManager.instance.isAnteMeridiem()) {
            setTheme(R.style.FullScreen_Light_Mode)
            ScreenBrightness.instance.setBrightnessValue(this, 80)
        } else {
            setTheme(R.style.FullScreen_Dark_Mode)
            ScreenBrightness.instance.setBrightnessValue(this, 20)
        }
        setSystemUiVisibility()
        setContentView(R.layout.home_screen)
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        from_date = sharedPreferences?.getString(SharedPreferencesHelper.from_date,null)
        submitApplicationVersion()
        initializePlayer()
        sideMenuFragment = SideMenuFragment()
        openPatternBtn.setOnClickListener { openPattern() }
        helper.requestRuntimePermissions()
        getList =  advertisementTracking.getList(DisplayManager.instance.getCurrentDate())
        checkDateAndUploadResult()
        addFragments()
    }



    private fun setSystemUiVisibility() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    @SuppressLint("CommitPrefEdits")
    private fun submitApplicationVersion() {
        val submittedVersion = sharedPreferences?.getString(SharedPreferencesHelper.submitted_version, null)
        val currentVersion = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
        if (currentVersion.isNotEmpty()){
            if (submittedVersion.isNullOrEmpty() || submittedVersion != currentVersion){
                deviceId = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)
                val packageName = BuildConfig.APPLICATION_ID
                deviceId?.let {
                    val submitApplicationVersionCredentials = SubmitApplicationVersionCredentials(packageName, currentVersion)
                    sendCurrentVersionToServer(it, submitApplicationVersionCredentials)
                }
            }
        }
    }

    private fun sendCurrentVersionToServer(deviceId: String, submitApplicationVersionCredentials: SubmitApplicationVersionCredentials){
        //Log.d("SubmitApplicationVersionResponse","deviceId: $deviceId")
        val submitApplicationVersionViewModel: SubmitApplicationVersionViewModel by viewModels()
        submitApplicationVersionViewModel.submitApplicationVersion(deviceId, submitApplicationVersionCredentials, this).observe(this, Observer<SubmitApplicationVersionResponse> {
            if (it != null) {
                //Log.d("SubmitApplicationVersionResponse","mResponseErrors: ${it.mResponseErrors?.errors?.first()?.statusCode}")
                if (it.isSuccess) {
                    //Log.d("SubmitApplicationVersionResponse","successResponse: ${it.isSuccess}")
                    editor?.putString(SharedPreferencesHelper.submitted_version, submitApplicationVersionCredentials.versions)?.apply()
                }
            }
        })
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        demoVideoPlayer.player = player
        val mediaSource = buildRawMediaSource()
        mediaSource?.let {
            player!!.apply {
                setMediaSource(it)
                prepare()
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = true
            }
        }
    }
    private fun buildRawMediaSource(): MediaSource? {
        val rawDataSource = RawResourceDataSource(this)
        // open the /raw resource file
        rawDataSource.open(DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.offline_video)))
        // Create media Item
        val mediaItem = MediaItem.fromUri(rawDataSource.uri!!)
        // create a media source with the raw DataSource
        val mediaSource = ProgressiveMediaSource.Factory { rawDataSource }
                .createMediaSource(mediaItem)
        return mediaSource
    }

    fun playVideo(){
        player?.play()
    }
    fun stopVideo(){
        player?.pause()
    }

    private fun checkDateAndUploadResult(){


        from_date?.let {from_date->
            Log.d("Report","${getJsonArray()}")
            if(DisplayManager.instance.checkDate(from_date.toLong())){

                val postReportViewModel: ContentViewModel by viewModels()
                postReportViewModel.postReport(this,getJsonArray(),deviceId!!).observe(this , Observer<ReportResponse> {
                    if(it.isSuccess){

                        advertisementTracking.deleteData(DisplayManager.instance.getCurrentDate())
                        editor?.putString(SharedPreferencesHelper.from_date, DisplayManager.instance.getCurrentDate().toString())
                        editor?.commit()

                    }

                })

            }

        }
    }



    private fun getJsonArray(): JSONObject {
        getList =  advertisementTracking.getList(DisplayManager.instance.getCurrentDate())
        val jsonObject = JSONObject()
        val jsonArray = JsonArray()
        getList?.forEach {
            //Log.d("Report","ID ${it.id}, AD ${it.advertisementId}, Date ${it.date}, Mo ${it.morning}, No ${it.noon}, Ev ${it.evening}, Ni ${it.evening}")

            val jsonObject = JsonObject().apply{
                addProperty("date",it.date)
                addProperty("advertisementId",it.advertisementId)
                add("slots",getJsonArrayOfSlot(it.morning,it.noon,it.evening,it.night))
            }
            jsonArray.add(jsonObject)
        }

        return jsonObject.put("analytics",jsonArray)

    }

    private fun getJsonArrayOfSlot(morning:Int,noon:Int,evening:Int,night:Int):JsonArray{
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        if(morning != 0){
            jsonObject.addProperty("mo",morning)
        }
        if(noon != 0){
            jsonObject.addProperty("no",noon)
        }
        if(evening != 0){
            jsonObject.addProperty("ev",evening)
        }
        if(night != 0){
            jsonObject.addProperty("ni",night)
        }
        jsonArray.add(jsonObject)

        return jsonArray

    }

    override fun onDestroy() {
        if (DisplayManager.instance.wasRegistered(this)) DisplayManager.instance.unregisterActivity(this)
        super.onDestroy()
    }

    override fun onStart() {
        registerNetworkCallback(true)
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        registerNetworkCallback(false)
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    private fun addFragments() {
        //Log.d("Network-Status","addFragments")
        supportFragmentManager.beginTransaction().replace(R.id.contentFragment_container, ContentFragment(), "Content_Fragment").commit()
        if (sideMenuFragment != null) supportFragmentManager.beginTransaction().replace(R.id.sideMenuFragment_container, sideMenuFragment!!, "SideMenu_Fragment").commit()
    }
    private fun removeFragments() {
        val contentFragment = supportFragmentManager.findFragmentByTag("Content_Fragment")
        val sideMenuFragment = supportFragmentManager.findFragmentByTag("SideMenu_Fragment")
        contentFragment?.let { supportFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss() }
        sideMenuFragment?.let { supportFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss() }
    }
    override fun onPermissionsOkay() {}

    private fun openPattern() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
            helper.showAdminVerificationDialog()
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }
    override fun onModeChange() {
        removeFragments()
        recreate()
    }
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            if (!isHotspotAlive) turnOnHotspot()
            try {
                this@HomeActivity.runOnUiThread(java.lang.Runnable {

                    activityCover.visibility = View.GONE
                    activityVideoCover.visibility = View.GONE
                    demoVideoPlayer.visibility = View.GONE
                    if(player!!.isPlaying){
                        stopVideo()
                    }
                })

            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        override fun onLost(network: Network?) {
            //Log.d("Network-Status","onLost")
            if (isHotspotAlive) turnOffHotspot()
        }
    }

    private fun turnOnHotspot() {
        //Log.d("Network-Status","turnOnHotspot")
        val intent = Intent(getString(R.string.intent_action_turnon))
        sendImplicitBroadcast(intent)
        isHotspotAlive = true
    }

    private fun turnOffHotspot() {
        //Log.d("Network-Status","turnOffHotspot")
        val intent = Intent(getString(R.string.intent_action_turnoff))
        sendImplicitBroadcast(intent)
        isHotspotAlive = false
    }
    private fun sendImplicitBroadcast(i: Intent) {
        val pm: PackageManager = this.packageManager
        val matches = pm.queryBroadcastReceivers(i, 0)
        for (resolveInfo in matches) {
            val explicit = Intent(i)
            val cn = ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name)
            explicit.component = cn
            this.sendBroadcast(explicit)
        }
    }

    private fun registerNetworkCallback(register: Boolean) {
        if (register) {
            try {
                connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        } else {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

    @Subscribe()
    fun onEvent(demoVideo: DemoVideo){
        try {
            this@HomeActivity.runOnUiThread(java.lang.Runnable {

                if(demoVideo.isPlay){
                    textViewError.visibility = View.VISIBLE
                    textViewError.text = demoVideo.errorMessage
                    activityVideoCover.visibility = View.VISIBLE
                    demoVideoPlayer.visibility = View.VISIBLE
                    playVideo()
                }else{
                    if(activityVideoCover.visibility == View.VISIBLE){
                        textViewError.visibility = View.GONE
                        activityVideoCover.visibility = View.GONE
                        demoVideoPlayer.visibility = View.GONE
                        stopVideo()

                    }else{
                        textViewError.visibility = View.GONE
                        activityVideoCover.visibility = View.GONE
                        demoVideoPlayer.visibility = View.GONE
                    }

                }

            })
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }


}