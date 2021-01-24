package com.routesme.taxi.MVVM.View.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.Class.DisplayManager
import com.routesme.taxi.Class.HomeScreenHelper
import com.routesme.taxi.Class.ScreenBrightness
import com.routesme.taxi.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi.LocationTrackingService.Class.AdvertisementDataLayer
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
import com.routesme.taxi.database.ResponseBody
import com.routesme.taxi.database.database.AdvertisementDatabase
import com.routesme.taxi.database.factory.ViewModelFactory
import com.routesme.taxi.database.helper.DatabaseHelperImpl
import com.routesme.taxi.database.viewmodel.RoomDBViewModel
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.service.AdvertisementService
import kotlinx.android.synthetic.main.home_screen.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class HomeActivity : PermissionsActivity(), IModeChanging,CoroutineScope by MainScope(),ServiceConnection{
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
    private var advertisementService: AdvertisementService? = null
    private lateinit var viewModel: RoomDBViewModel
    private var getList:List<AdvertisementTracking>?=null
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
        deviceId = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)
        viewModel =  ViewModelProvider(this, ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(this)))).get(RoomDBViewModel::class.java)
        submitApplicationVersion()
        checkDateAndUploadResult()
        launch {initializePlayer()}
        sideMenuFragment = SideMenuFragment()
        turnOnHotspot()
        openPatternBtn.setOnClickListener { openPattern() }
        helper.requestRuntimePermissions()
        addFragments()
        startAdvertisementService()
        observeAnalytics()
    }

    private fun observeAnalytics(){

        viewModel.getReport(DateHelper.instance.getCurrentDate()).observe(this, Observer {

            when(it.status){

                ResponseBody.Status.SUCCESS -> {


                    //it.data?.let { users -> renderList(users) }

                }
                ResponseBody.Status.LOADING -> {

                }
                ResponseBody.Status.ERROR -> {
                    //Handle Error

                }
            }
        })


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
                val packageName = BuildConfig.APPLICATION_ID
                deviceId?.let {
                    val submitApplicationVersionCredentials = SubmitApplicationVersionCredentials(packageName, currentVersion)
                    sendCurrentVersionToServer(it, submitApplicationVersionCredentials)
                }
            }
        }
    }

    private fun sendCurrentVersionToServer(deviceId: String, submitApplicationVersionCredentials: SubmitApplicationVersionCredentials){
        val submitApplicationVersionViewModel: SubmitApplicationVersionViewModel by viewModels()
        submitApplicationVersionViewModel.submitApplicationVersion(deviceId, submitApplicationVersionCredentials, this).observe(this, Observer<SubmitApplicationVersionResponse> {
            if (it != null) {
                if (it.isSuccess) {

                    editor?.putString(SharedPreferencesHelper.submitted_version, submitApplicationVersionCredentials.versions)?.apply()

                }
            }
        })
    }

    private suspend fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        demoVideoPlayer.player = player
        val mediaSource = buildRawMediaSource()
        withContext(Dispatchers.Main){
            mediaSource?.let {
                player?.apply {
                    setMediaSource(it)
                    prepare()
                    repeatMode = Player.REPEAT_MODE_ONE
                    playWhenReady = true
                }
            }
        }
    }

    private suspend fun buildRawMediaSource(): MediaSource? {
        return withContext(Dispatchers.Default){
            val rawDataSource = RawResourceDataSource(this@HomeActivity)
            rawDataSource.open(DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.offline_video)))
            val mediaItem = MediaItem.fromUri(rawDataSource.uri!!)
            rawDataSource.close()
            val mediaSource = ProgressiveMediaSource.Factory { rawDataSource }.createMediaSource(mediaItem)
            return@withContext mediaSource
        }
    }
    private fun checkDateAndUploadResult(){
        from_date?.let {from_date->
            if(DateHelper.instance.checkDate(from_date.toLong())){
                val postReportViewModel: ContentViewModel by viewModels()
                getJsonArray().let { list->
                    deviceId?.let {deviceId->
                        postReportViewModel.postReport(this,list,deviceId).observe(this , Observer<ReportResponse> {
                            if(it.isSuccess){
                                advertisementTracking.deleteData(DateHelper.instance.getCurrentDate())
                                editor?.putString(SharedPreferencesHelper.from_date, DateHelper.instance.getCurrentDate().toString())
                                editor?.commit()
                            }
                        })
                    }
                }
            }
        }
    }

    private fun getJsonArray(): JsonArray {
        getList =  advertisementTracking.getList(DateHelper.instance.getCurrentDate())
        val jsonArray = JsonArray()
        getList?.forEach {
            val jsonObject = JsonObject().apply{
                addProperty("date",it.date/1000)
                addProperty("advertisementId",it.advertisementId)
                addProperty("mediaType",it.media_type)
                add("slots",getJsonArrayOfSlot(it.morning,it.noon,it.evening,it.night))
            }
            jsonArray.add(jsonObject)
        }

        return jsonArray

    }

    private fun getJsonArrayOfSlot(morning:Int,noon:Int,evening:Int,night:Int):JsonArray{
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        if(morning != 0){
            jsonObject.addProperty("period","mo")
            jsonObject.addProperty("value",morning)
        }
        if(noon != 0){
            jsonObject.addProperty("period","no")
            jsonObject.addProperty("value",noon)
        }
        if(evening != 0){
            jsonObject.addProperty("period","ev")
            jsonObject.addProperty("value",evening)
        }
        if(night != 0){
            jsonObject.addProperty("period","ni")
            jsonObject.addProperty("value",night)
        }
        jsonArray.add(jsonObject)

        return jsonArray

    }


    private fun addFragments() {

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

    private fun turnOnHotspot() {

        val intent = Intent(getString(R.string.intent_action_turnon))
        sendImplicitBroadcast(intent)
        isHotspotAlive = true
    }

    private fun turnOffHotspot() {

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

    override fun onServiceDisconnected(p0: ComponentName?) {

        Log.d("Services","Services DisConnected")

    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {


        Log.d("Services","Services Connected")

    }
    private  fun startAdvertisementService(){

        startService(Intent(this, AdvertisementService::class.java))

    }

    private fun stopAdvertisementService(){

        stopService(Intent(this@HomeActivity, AdvertisementService::class.java))

    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        turnOffHotspot()
        if (DisplayManager.instance.wasRegistered(this)) DisplayManager.instance.unregisterActivity(this)
        cancel()
        stopAdvertisementService()
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    fun playVideo(){

        player?.play()

    }

    fun stopVideo(){

        player?.pause()

    }

}