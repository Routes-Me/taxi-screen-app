package com.routesme.vehicles.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.github.pedrovgs.lynx.LynxActivity
import com.github.pedrovgs.lynx.LynxConfig
import com.routesme.vehicles.App
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.view.activity.LoginActivity
import org.greenrobot.eventbus.EventBus


class AdminConsoleHelper(val activity: Activity) {
    private val READ_PHONE_STATE_REQUEST_CODE = 202
    private val sharedPreferences = activity.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
    private val appPackageName = activity.packageName
    private val locationPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private var locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    companion object {
        private const val defaultValue = "- -"
    }

    fun plateNumber() = sharedPreferences.getString(SharedPreferencesHelper.vehicle_plate_number, defaultValue)
    fun institutionName() = sharedPreferences.getString(SharedPreferencesHelper.institution_name, defaultValue)
    fun appVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
    fun simSerialNumber() = sharedPreferences.getString(SharedPreferencesHelper.sim_serial_number, defaultValue)
    fun imei() = sharedPreferences.getString(SharedPreferencesHelper.device_serial_number, defaultValue)
    fun referralCode() = sharedPreferences.getString(SharedPreferencesHelper.referral_code, defaultValue)
    fun referralUrl() = sharedPreferences.getString(SharedPreferencesHelper.referral_url, defaultValue)
    fun technicalUserName() = sharedPreferences.getString(SharedPreferencesHelper.username, defaultValue)
    fun registrationDate() = sharedPreferences.getString(SharedPreferencesHelper.registration_date, defaultValue)

    fun getSimStatus():List<ICell>?{
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE)
            return null
        } else {
            val telephonyManager = App.instance.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
            val simStatus: String = when (telephonyManager.simState) {
                TelephonyManager.SIM_STATE_READY -> SimStates.READY.value
                TelephonyManager.SIM_STATE_ABSENT -> SimStates.ABSENT.value
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> SimStates.NETWORK_LOCKED.value
                TelephonyManager.SIM_STATE_PIN_REQUIRED -> SimStates.PIN_REQUIRED.value
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> SimStates.PUK_REQUIRED.value
                else -> SimStates.UNKNOWN.value
            }
            return mutableListOf<ICell>().apply {add(DetailCell("SIM Status",simStatus, true))}
        }
    }

    @SuppressLint("HardwareIds")
     fun getBuildInfo(): List<ICell>? {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_REQUEST_CODE)
            return null
        } else {
           return mutableListOf<ICell>().apply {
              // add(DetailCell("Device Serial Number", Build.getSerial(), true))
               add(DetailCell("Device Model", Build.MODEL, false))
           }
        }
    }

     fun getNetworkType(): List<ICell>? {
        val cm = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        val type = if(netInfo != null && netInfo.isConnected) {
            when (netInfo.type) {
                TYPE_WIFI -> NetworkType.WIFI.value
                TYPE_MOBILE -> NetworkType.SIM.value
                else -> NetworkType.OTHERS.value
            }
        }else{
            NetworkType.NOT_CONNECTED.value
        }
        return mutableListOf<ICell>().apply {add(DetailCell("Network Type",type, true))}
    }

    fun vehicleId() = sharedPreferences.getString(SharedPreferencesHelper.vehicle_id, defaultValue)
    fun deviceId() = sharedPreferences.getString(SharedPreferencesHelper.device_id, defaultValue)
    fun isMyAppDefaultLauncher(): DetailActionStatus {
        val filters: MutableList<IntentFilter> = ArrayList()
        val activities: List<ComponentName> = ArrayList()
        val packageManager = activity.packageManager as PackageManager
        val filter = IntentFilter(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        filters.add(filter)
        packageManager.getPreferredActivities(filters, activities, null)
        for (activity in activities) {
            if (appPackageName == activity.packageName) return DetailActionStatus.DONE
        }
        return DetailActionStatus.PENDING
    }

    fun openDefaultLauncherSetting() {
        activity.apply { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
    }

    fun isLocationPermissionsAllowed(): DetailActionStatus {
        for (p in locationPermissions) {
            if (ActivityCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) return DetailActionStatus.PENDING
        }
        return DetailActionStatus.DONE
    }

    fun openAppGeneralSettings() {
        activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", appPackageName, null)))
    }

    fun openSystemLogs() {
        val lynxConfig = LynxConfig()
        lynxConfig.setMaxNumberOfTracesToShow(2000000)
        activity.startActivity(Intent(LynxActivity.getIntent(activity, lynxConfig)))
    }

    fun sendLogOffRequestToActvitiy() {
        EventBus.getDefault().post(LogOff(true))

    }

    fun logOff() {

        sharedPreferences?.edit()?.clear()?.apply()
        activity.apply {
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    fun sendUpdateReferralInfoRequestToActivity() {
        EventBus.getDefault().post(UpdateReferralInfo(true))
    }

    fun isLocationProviderEnabled() = isGPSEnabled() || isNetworkEnabled()
    private fun isGPSEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    private fun isNetworkEnabled() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
enum class SimStates(val value: String) {READY("READY"), ABSENT("ABSENT"), NETWORK_LOCKED("NETWORK LOCKED"), PIN_REQUIRED("PIN REQUIRED"), PUK_REQUIRED("PUK REQUIRED"), UNKNOWN("UNKNOWN")}
enum class NetworkType(val value: String){ WIFI("WIFI"), SIM("SIM"), OTHERS("Others"), NOT_CONNECTED("Not Connected")}