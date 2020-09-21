package com.routesme.taxi_screen.MVVM.View.HomeScreen.Activity

import android.app.Activity
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import android.view.Window
import com.routesme.taxi_screen.Class.*
import com.routesme.taxi_screen.Hotspot_Configuration.PermissionsActivity
import com.routesme.taxi_screen.LocationTrackingService.Class.LocationTrackingService
import com.routesme.taxi_screen.MVVM.Model.IModeChanging
import com.routesme.taxi_screen.MVVM.Model.QRCodeCallback
import com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment.ContentFragment
import com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment.SideMenuFragment
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.home_screen.*

class HomeActivity : PermissionsActivity() , IModeChanging, QRCodeCallback {

    //Variable to store brightness value
    private var brightness = 0
    //Content resolver used as a handle to the system's settings
    private  var cResolver: ContentResolver? = null
    //Window object, that will store a reference to the current window
    private  var w: Window? = null

    private val homeScreenFunctions = HomeScreenFunctions(this)
    private var isHotspotOn = false
    private var locationTrackingService: LocationTrackingService? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var pressedTime: Long = 0
    private var clickTimes = 0
    private val displayManager = DisplayManager.instance
    private val operations = Operations.instance
    private var contentFragment: ContentFragment? = null
    private var sideMenuFragment: SideMenuFragment? = null

    companion object{
        @get:Synchronized
        val instance = HomeActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.currentActivity = this
        displayManager.registerActivity(this)
        if (displayManager.isAnteMeridiem()){setTheme(R.style.FullScreen_Light_Mode)}else{setTheme(R.style.FullScreen_Dark_Mode)}
        setContentView(R.layout.home_screen)
        brightnessSetup()
        updateBrightness()
        sharedPreferences = getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
       // homeScreenFunctions.firebaseAnalytics_Crashlytics(sharedPreferences.getString(SharedPreference.device_id, null))
        openPattern.setOnClickListener {openPatternClick()}
        homeScreenFunctions.hideNavigationBar()
        homeScreenFunctions.requestRuntimePermissions()
        turnOnHotspot()
        addFragments()
    }

    private fun brightnessSetup() {
        cResolver = contentResolver
        w = window

        try {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
            brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: SettingNotFoundException) {
            Log.e("Error", "Cannot access system brightness")
            e.printStackTrace()
        }
    }

    private fun updateBrightness(){
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        val layoutParams = w?.getAttributes()
        layoutParams?.screenBrightness = brightness / 255.toFloat()
        w?.attributes = layoutParams
    }

    override fun onDestroy() {
        removeFragments()
        if (displayManager.wasRegistered(this) )displayManager.unregisterActivity(this)
        super.onDestroy()
    }

    override fun onResume() {
        //homeScreenFunctions.hideNavigationBar()
        //homeScreenFunctions.requestRuntimePermissions()
        //turnOnHotspot()
        // startLocationTrackingService()
        //showFragments()
        super.onResume()
    }

    override fun onStart() {
       // operations.publish(deviceToken(),this)
        super.onStart()
    }

    override fun onStop() {
       // operations.unPublish(deviceToken(),this)
        super.onStop()
    }

    private fun startLocationTrackingService() {
        locationTrackingService = LocationTrackingService()
    }

    override fun onPause() {

        // if (locationTrackingService != null) locationTrackingService!!.stopLocationTrackingService()
        super.onPause()
    }

    private fun addFragments() {
        contentFragment = ContentFragment.instance
        sideMenuFragment = SideMenuFragment.instance
        if(contentFragment != null) supportFragmentManager.beginTransaction().replace(R.id.contentFragment_container, contentFragment!!).commit()
        if(sideMenuFragment != null) supportFragmentManager.beginTransaction().replace(R.id.sideMenuFragment_container, sideMenuFragment!!).commit()
    }

    private fun removeFragments() {
        contentFragment = null
        sideMenuFragment = null
    }

    private fun deviceToken() = "dF9bQgwjSxmY-Glapm-ZmL:APA91bH2OS7k9nX_fkiH1h6St1JB41Z50aUK0dU0XABvs_C6-5DNQaz78jYgM4bCQuyVC0o1Yju9TmMJl7NFux2cTQOPguGiBS4fevIP1tMoxvsYe3b_qo6K5wTXB56erjiEKyd6Cazc"

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
            homeScreenFunctions.showAdminVerificationDialog()
            clickTimes = 0
        }
        pressedTime = System.currentTimeMillis()
    }

    override fun onModeChange() {
        recreate()
    }

    override fun onVideoQRCodeChanged(promotion: com.routesme.taxi_screen.MVVM.Model.Promotion?) {
        sideMenuFragment?.changeVideoQRCode(promotion)
    }

    override fun onBannerQRCodeChanged(promotion: com.routesme.taxi_screen.MVVM.Model.Promotion?) {
        sideMenuFragment?.changeBannerQRCode(promotion)
    }
}