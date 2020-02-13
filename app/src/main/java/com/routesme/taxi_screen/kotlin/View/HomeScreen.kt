package com.routesme.taxi_screen.kotlin.View

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.routesme.taxi_screen.java.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi_screen.java.View.NewHomeScreen.Fragments.ContentFragment
import com.routesme.taxi_screen.java.View.NewHomeScreen.Fragments.SideMenuFragment
import com.routesme.taxi_screen.kotlin.HomeScreenFunctions
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Class.LocationTrackingService
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
        turnOnHotspot()
        openPattern.setOnClickListener {openPatternClick()}
    }

    override fun onResume() {
        homeScreenFunctions.hideNavigationBar()
        homeScreenFunctions.firebaseAnalytics_Crashlytics(sharedPreferences.getString("tabletSerialNo", null))
        showFragments()
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
            homeScreenFunctions.sendImplicitBroadcast(Intent(getString(R.string.intent_action_turnon)))
            isHotspotOn = true
        }
    }
    private fun openPatternClick() {
        clickTimes++
        if (pressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10){
            homeScreenFunctions.showAdminVerificationDialog(sharedPreferences.getString("tabletPassword", null))
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }
}