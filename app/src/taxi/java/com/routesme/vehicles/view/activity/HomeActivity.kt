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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.helper.*
import com.routesme.vehicles.view.fragment.ContentFragment
import com.routesme.vehicles.viewmodel.SubmitApplicationVersionViewModel
import com.routesme.vehicles.view.events.DemoVideo
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.nearby.NearByOperation
import com.routesme.vehicles.view.events.PublishNearBy
import com.routesme.vehicles.viewmodel.TerminalViewModel
import kotlinx.android.synthetic.taxi.home_screen.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

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
    private  var terminalId:String?=null

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
        from_date = sharedPreferences?.getString(SharedPreferencesHelper.from_date,null)
        deviceId = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)
        terminalId = sharedPreferences?.getString(SharedPreferencesHelper.terminal_id, null)
        //if(terminalId == null) registerTerminal(getParemeter(deviceId!!))
        submitApplicationVersion()
        launch {initializePlayer()}
        turnOnHotspot()
        openPatternBtn.setOnClickListener { openPattern() }
        helper.requestRuntimePermissions()
        addFragments()
        setSystemUiVisibility()
    }

    private fun registerTerminal(parameter:Parameter) {
        val terminalViewModel : TerminalViewModel by viewModels()
        terminalViewModel.createTerminal(parameter,this).observe(this, Observer<TerminalResponse> {
            if (it != null) {
                if (it.isSuccess) {
                    editor?.apply {
                        putString(SharedPreferencesHelper.terminal_id, it.terminalId)
                    }?.apply()
                } else {

                }
            }
        })
    }

    private fun getParemeter(deviceId: String): Parameter {
        val parameter = Parameter()
        parameter.DeviceId = deviceId
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            // Get new FCM registration token
            parameter.NotificationIdentifier  = task.result
            Log.d("FCM_TOKEN", task.result)
        })
        return parameter
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

    private fun getScreenInfo(): Parameter {
        //return sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!
        val item = Parameter()
        item.DeviceId = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)
        item.plateNo = sharedPreferences?.getString(SharedPreferencesHelper.vehicle_plate_number, null)
        return item
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
    @Subscribe()
    fun onEvent(boolean: PublishNearBy){
        Log.d("Publish","Event Trigger")
        NearByOperation.instance.publish(getScreenInfo(),this)

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
        NearByOperation.instance.publish(getScreenInfo(),this)
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        Log.d("LifeCycle","Stop")
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