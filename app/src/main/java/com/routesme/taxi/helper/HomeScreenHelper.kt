package com.routesme.taxi.helper

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.routesme.taxi.R
import com.routesme.taxi.view.activity.AdminConsolePanel
import kotlinx.android.synthetic.main.exit_pattern_dialog.*

class HomeScreenHelper(val activity: Activity) {

    fun requestRuntimePermissions() {
        val permissionsList = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE)
        if (!hasPermissions(*permissionsList)) ActivityCompat.requestPermissions(activity, permissionsList, 1)
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (p in permissions) {
                if (ActivityCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun showAdminVerificationDialog() {
        val exitPassword = exitPassword()
        val adminVerificationDialog = Dialog(activity)
        adminVerificationDialog.setContentView(R.layout.exit_pattern_dialog)
        adminVerificationDialog.setCancelable(false)
        adminVerificationDialog.show()
        val adminVerificationPattern = adminVerificationDialog.admin_verification_pattern
        adminVerificationPattern.addPatternLockListener(object : PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                val finalPattern = PatternLockUtils.patternToString(adminVerificationPattern, pattern)

                if (finalPattern == exitPassword) {
                    adminVerificationDialog.dismiss()
                    openAdminConsolePanel()
                } else {
                    adminVerificationPattern.clearPattern()
                    adminVerificationDialog.dismiss()
                }
            }

            override fun onCleared() {}
            override fun onStarted() {}
            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {}
        })
    }

    private fun openAdminConsolePanel() {
        activity.apply { startActivity(Intent(activity, AdminConsolePanel::class.java)); }
    }

    private fun exitPassword(): String? = Helper.getConfigValue("exitPassword", R.raw.config)
}