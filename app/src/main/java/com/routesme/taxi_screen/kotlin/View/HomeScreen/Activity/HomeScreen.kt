package com.routesme.taxi_screen.kotlin.View.HomeScreen.Activity

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.routesme.taxi_screen.java.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi_screen.kotlin.Class.HomeScreenFunctions
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Class.LocationTrackingService
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.ContentFragment
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.SideMenuFragment
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.home_screen.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeScreen : PermissionsActivity() {

    private val homeScreenFunctions = HomeScreenFunctions(this)
    private var isHotspotOn = false
    private var locationTrackingService: LocationTrackingService? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var pressedTime: Long = 0
    private var clickTimes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        sharedPreferences = getSharedPreferences("userData", Activity.MODE_PRIVATE);
        homeScreenFunctions.requestRuntimePermissions()
        openPattern.setOnClickListener {openPatternClick()}
    }

    override fun onResume() {
        homeScreenFunctions.hideNavigationBar()
        homeScreenFunctions.firebaseAnalytics_Crashlytics(sharedPreferences.getString("tabletSerialNo", null))
        showFragments()
       // turnOnHotspot()
        startLocationTrackingService()
        super.onResume()
    }

    override fun onPause() {
        if (locationTrackingService != null) locationTrackingService!!.stopLocationTrackingService()
        super.onPause()
    }

    private fun showFragments() {
        supportFragmentManager.beginTransaction().replace(R.id.contentFragment_container, ContentFragment()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.sideMenuFragment_container, SideMenuFragment()).commit()
    }

    private fun startLocationTrackingService() {
        locationTrackingService = LocationTrackingService(this)
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
}