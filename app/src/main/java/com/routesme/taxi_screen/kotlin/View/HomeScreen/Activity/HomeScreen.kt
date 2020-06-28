package com.routesme.taxi_screen.kotlin.View.HomeScreen.Activity

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.routesme.taxi_screen.java.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.DisplayManager
import com.routesme.taxi_screen.kotlin.Class.HomeScreenFunctions
import com.routesme.taxi_screen.kotlin.Class.Operations
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Class.LocationTrackingService
import com.routesme.taxi_screen.kotlin.Model.IModeChanging
import com.routesme.taxi_screen.kotlin.Model.PaymentData
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.ContentFragment
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.PaymentFragment
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.SideMenuFragment
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.home_screen.*

class HomeScreen : PermissionsActivity() ,IModeChanging{

    private val homeScreenFunctions = HomeScreenFunctions(this)
    private var isHotspotOn = false
    private var locationTrackingService: LocationTrackingService? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val displayManager = DisplayManager.instance


    companion object{
        @get:Synchronized
        val instance = HomeScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.currentActivity = this
        displayManager.registerActivity(this)
        if (displayManager.isAnteMeridiem()){setTheme(R.style.FullScreen_Light_Mode)}else{setTheme(R.style.FullScreen_Dark_Mode)}
        setContentView(R.layout.home_screen)

        showFragments()

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE)
        homeScreenFunctions.firebaseAnalytics_Crashlytics(sharedPreferences.getString("tabletSerialNo", null))
        openPattern.setOnClickListener {openPatternClick()}
    }

    override fun onDestroy() {
        displayManager.unregisterActivity(this)
        super.onDestroy()
    }

    override fun onResume() {
        homeScreenFunctions.hideNavigationBar()
        homeScreenFunctions.requestRuntimePermissions()
        turnOnHotspot()
        //startLocationTrackingService()
        super.onResume()
    }

    private fun startLocationTrackingService() {
        locationTrackingService = LocationTrackingService()
    }

    override fun onPause() {
       // if (locationTrackingService != null) locationTrackingService!!.stopLocationTrackingService()
        super.onPause()
    }

    private fun showFragments() {
        supportFragmentManager.beginTransaction().replace(R.id.contentFragment_container, ContentFragment.instance).commit()
        supportFragmentManager.beginTransaction().replace(R.id.sideMenuFragment_container, SideMenuFragment.instance).commit()
    }

    override fun onPermissionsOkay() {}

    private fun turnOnHotspot() {
        if (!isHotspotOn) {
            sendImplicitBroadcast(Intent(getString(R.string.intent_action_turnon)))
            isHotspotOn = true
        }
    }

    private fun sendImplicitBroadcast(i: Intent) {
        val matches = packageManager.queryBroadcastReceivers(i, 0)
        for (resolveInfo in matches) {
            Intent(i).component = ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name)
            sendBroadcast(Intent(i))
        }
    }
    private fun openPatternClick() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10){
            homeScreenFunctions.showAdminVerificationDialog(sharedPreferences.getString("tabletPassword", null).toString())
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }

    override fun onModeChange() {
        recreate()
    }



}