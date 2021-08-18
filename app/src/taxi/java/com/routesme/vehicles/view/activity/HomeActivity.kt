package com.routesme.vehicles.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.routesme.vehicles.App
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.helper.*
import com.routesme.vehicles.view.fragment.ContentFragment
import com.routesme.vehicles.viewmodel.SubmitApplicationVersionViewModel
import com.routesme.vehicles.view.events.DemoVideo
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.uplevels.DeviceInformation
import kotlinx.android.synthetic.taxi.home_screen.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : com.routesme.vehicles.view.activity.PermissionsActivity(), IModeChanging,CoroutineScope by MainScope(){
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private val helper by lazy { HomeScreenHelper(this) }
    private var isHotspotAlive = false
    private var pressedTime: Long = 0
    private lateinit var mView: View
    private var clickTimes = 0
    private var player : SimpleExoPlayer?=null
    private  var from_date:String?=null
    private  var deviceId:String?=null

   // private val nearbyData = NearbyData("1128","88977","TerminalId44","inst756")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DisplayManager.instance.registerActivity(this)
        if (DisplayManager.instance.isAnteMeridiem()) {
            DisplayManager.instance.currentMode = Mode.Light
            setTheme(R.style.FullScreen_Light_Mode)
            ScreenBrightness.instance.setBrightnessValue(this, 80)
        } else {
            DisplayManager.instance.currentMode = Mode.Dark
            setTheme(R.style.FullScreen_Dark_Mode)
            ScreenBrightness.instance.setBrightnessValue(this, 20)
        }
        setContentView(R.layout.home_screen)
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        from_date = App.instance.deviceInformation.fromDate
        deviceId = App.instance.deviceInformation.deviceId
        submitApplicationVersion()
        launch {initializePlayer()}
       // turnOnHotspot()
        openPatternBtn.setOnClickListener { openPattern() }
        helper.requestRuntimePermissions()
        addFragments()
        setSystemUiVisibility()
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
        player = SimpleExoPlayer.Builder(demoVideoPlayer.context).build()
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
    private fun addFragments() {
        supportFragmentManager.commit {
            replace<ContentFragment>(R.id.contentFragment_container)
        }
    }

    private fun removeFragments() {
        val contentFragment = supportFragmentManager.findFragmentByTag("Content_Fragment")
        contentFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(demoVideo: DemoVideo){
        try {
                if(demoVideo.isPlay){
                    textViewError.visibility = View.VISIBLE
                    textViewError.text = demoVideo.errorMessage
                    activityVideoCover.visibility = View.VISIBLE
                    demoVideoPlayer.visibility = View.VISIBLE
                    playVideo()
                }else {
                    textViewError.visibility = View.GONE
                    activityVideoCover.visibility = View.GONE
                    demoVideoPlayer.visibility = View.GONE
                    if(player?.isPlaying!!) stopVideo()




                }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }

    fun playVideo(){

        player?.play()

    }

    fun stopVideo(){

        player?.pause()

    }

    override fun onDestroy() {
        super.onDestroy()
        if(player !=null){
            player?.release()
            player = null
        }
        turnOffHotspot()
        removeFragments()
        if (DisplayManager.instance.wasRegistered(this)) DisplayManager.instance.unregisterActivity(this)
        cancel()
    }
    override fun onStart() {
        EventBus.getDefault().register(this)
        NearByOperation.instance.publish(NearByOperation.instance.getNearbyDataJson(getNearbyData()),this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        NearByOperation.instance.unPublish(NearByOperation.instance.getNearbyDataJson(getNearbyData()),this)
        super.onStop()
    }

    @Subscribe()
    fun onEvent(publishNearBy: PublishNearby){
        Log.d("NearbyMessagesApi","Publisher.. Republish after expired")
        if (publishNearBy.isPublish) NearByOperation.instance.publish(NearByOperation.instance.getNearbyDataJson(getNearbyData()),this)
    }

    private fun getNearbyData() = NearbyData().apply {
         deviceId = App.instance.deviceInformation.deviceId
         plateNumber = App.instance.deviceInformation.vehiclePlateNumber
         terminalId = App.instance.deviceInformation.terminalId
         institutionId = App.instance.deviceInformation.institutionId
     }
}