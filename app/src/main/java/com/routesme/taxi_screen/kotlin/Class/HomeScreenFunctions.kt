package com.routesme.taxi_screen.kotlin.Class

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.exit_pattern_dialog.*
import kotlin.system.exitProcess

class HomeScreenFunctions(val activity: Activity) {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun hideNavigationBar() {
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        activity.window.decorView.systemUiVisibility = flags
    }

    fun firebaseAnalytics_Crashlytics(tabletSerialNo: String?) {
        Crashlytics.setUserIdentifier(tabletSerialNo)
        FirebaseAnalytics.getInstance(activity).setUserId(tabletSerialNo)
    }

    fun requestRuntimePermissions() {
        val permissionsList = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE)
        if (!hasPermissions(*permissionsList)) ActivityCompat.requestPermissions(activity, permissionsList, 1)
    }

    private fun hasPermissions(vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (p in permissions) {
                if (ActivityCompat.checkSelfPermission(activity, p!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun sendImplicitBroadcast(i: Intent) {
        val matches = activity.packageManager.queryBroadcastReceivers(i, 0)
        for (resolveInfo in matches) {
            Intent(i).component = ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name)
            activity.sendBroadcast(Intent(i))
        }
    }

    fun showAdminVerificationDialog(tabletPassword: String) {
        val adminVerificationDialog = Dialog(activity)
        adminVerificationDialog.setContentView(R.layout.exit_pattern_dialog)
        adminVerificationDialog.setCancelable(false)
        adminVerificationDialog.show()
        val adminVerificationPattern = adminVerificationDialog.admin_verification_pattern
        adminVerificationPattern.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                val finalPattern = PatternLockUtils.patternToString(adminVerificationPattern, pattern)

                if (finalPattern == tabletPassword) {
                    openSettings();
                } else {
                    adminVerificationPattern.clearPattern();
                    adminVerificationDialog.dismiss();
                }
            }

            override fun onCleared() {}
            override fun onStarted() {}
            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {}
        })
    }

    private fun openSettings() {
        val settingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) Intent(Settings.ACTION_HOME_SETTINGS) else Intent(Settings.ACTION_SETTINGS)
        activity.startActivity(settingIntent)
        activity.finish()
        exitProcess(0)
    }
}