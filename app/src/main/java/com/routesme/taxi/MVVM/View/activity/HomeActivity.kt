package com.routesme.taxi.MVVM.View.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.routesme.taxi.Class.DisplayManager
import com.routesme.taxi.Class.HomeScreenHelper
import com.routesme.taxi.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi.MVVM.Model.Data
import com.routesme.taxi.MVVM.Model.IModeChanging
import com.routesme.taxi.MVVM.Model.Promotion
import com.routesme.taxi.MVVM.View.fragment.ContentFragment
import com.routesme.taxi.MVVM.View.fragment.SideMenuFragment
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.R
import kotlinx.android.synthetic.main.home_screen.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class HomeActivity : PermissionsActivity(), IModeChanging {
    private val helper = HomeScreenHelper(this)
    private var isHotspotAlive = false
    private var pressedTime: Long = 0
    private lateinit var mView: View
    private var clickTimes = 0
    private var sideMenuFragment: SideMenuFragment? = null
    private var player : SimpleExoPlayer?=null
    private val connectivityManager by lazy { getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DisplayManager.instance.registerActivity(this)
        if (DisplayManager.instance.isAnteMeridiem()) {
            setTheme(R.style.FullScreen_Light_Mode)
        } else {
            setTheme(R.style.FullScreen_Dark_Mode)
        }
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.home_screen)
        initializePlayer()
        sideMenuFragment = SideMenuFragment()
        openPatternBtn.setOnClickListener { openPattern() }
        helper.requestRuntimePermissions()
        addFragments()

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
        Log.d("Network-Status","addFragments")
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
            Log.d("Network-Status","onAvailable")
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
            Log.d("Network-Status","onLost")
            if (isHotspotAlive) turnOffHotspot()
        }
    }

    private fun turnOnHotspot() {
        Log.d("Network-Status","turnOnHotspot")
        val intent = Intent(getString(R.string.intent_action_turnon))
        sendImplicitBroadcast(intent)
        isHotspotAlive = true
    }

    private fun turnOffHotspot() {
        Log.d("Network-Status","turnOffHotspot")
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
        Log.d("Tag","HELLO")

        try {
            this@HomeActivity.runOnUiThread(java.lang.Runnable {
                activityVideoCover.visibility = View.VISIBLE
                demoVideoPlayer.visibility = View.VISIBLE
                playVideo()
            })
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }
}