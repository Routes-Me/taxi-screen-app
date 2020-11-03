package com.routesme.taxi.MVVM.View.activity

import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.routesme.taxi.Class.*
import com.routesme.taxi.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi.MVVM.Model.IModeChanging
import com.routesme.taxi.MVVM.Model.QRCodeCallback
import com.routesme.taxi.MVVM.View.fragment.ContentFragment
import com.routesme.taxi.MVVM.View.fragment.SideMenuFragment
import com.routesme.taxi.R
import kotlinx.android.synthetic.main.home_screen.*

class HomeActivity : PermissionsActivity(), IModeChanging, QRCodeCallback {
    private val helper = HomeScreenHelper(this)
    private var isHotspotOn = false
    private var pressedTime: Long = 0
    private var clickTimes = 0
    private var sideMenuFragment: SideMenuFragment? = null
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
        sideMenuFragment = SideMenuFragment()
        openPatternBtn.setOnClickListener { openPattern() }
        helper.requestRuntimePermissions()
        registerNetworkCallback(true)
    }
    override fun onDestroy() {
        if (DisplayManager.instance.wasRegistered(this)) DisplayManager.instance.unregisterActivity(this)
        registerNetworkCallback(false)
        super.onDestroy()
    }
    private fun addFragments() {
        supportFragmentManager.beginTransaction().replace(R.id.contentFragment_container, ContentFragment(), "Content_Fragment").commit()
        if (sideMenuFragment != null) supportFragmentManager.beginTransaction().replace(R.id.sideMenuFragment_container, sideMenuFragment!!, "SideMenu_Fragment").commit()
    }
    private fun removeFragments() {
        val contentFragment = supportFragmentManager.findFragmentByTag("Content_Fragment")
        val sideMenuFragment = supportFragmentManager.findFragmentByTag("SideMenu_Fragment")
        contentFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
        sideMenuFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
    }
    override fun onPermissionsOkay() {}
    private fun turnOnHotspot() {
        if (!isHotspotOn) {
            val intent = Intent(getString(R.string.intent_action_turnon))
            sendImplicitBroadcast(intent)
            isHotspotOn = true
        }
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
    override fun onVideoQRCodeChanged(promotion: com.routesme.taxi.MVVM.Model.Promotion?) {
        sideMenuFragment?.changeVideoQRCode(promotion)
    }
    override fun onBannerQRCodeChanged(promotion: com.routesme.taxi.MVVM.Model.Promotion?) {
        sideMenuFragment?.changeBannerQRCode(promotion)
    }
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            turnOnHotspot()
            addFragments()
            Toast.makeText(this@HomeActivity,"Network Available",Toast.LENGTH_LONG).show()
            try {
                this@HomeActivity.runOnUiThread(java.lang.Runnable {
                    activityCover.visibility = View.GONE
                })
                //connectivityManager.unregisterNetworkCallback(this)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        override fun onLost(network: Network?) {
            /*
              removeFragments()
            try {
                this@HomeActivity.runOnUiThread(java.lang.Runnable {
                    activityCover.visibility = View.VISIBLE
                })
                //connectivityManager.unregisterNetworkCallback(this)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            */
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
}