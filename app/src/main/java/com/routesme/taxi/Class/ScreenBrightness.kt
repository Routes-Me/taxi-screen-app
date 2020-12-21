package com.routesme.taxi.Class

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi

class ScreenBrightness {

    companion object {
        val instance = ScreenBrightness()
    }

    fun setBrightnessValue(context: Context, brightnessValue: Int) {
        // Check whether has the write settings permission or not.
        val settingsCanWrite = hasWriteSettingsPermission(context)

        // If do not have then open the Can modify system settings panel.
        if (!settingsCanWrite) {
            changeWriteSettingsPermission(context)
        } else {
            changeScreenBrightness(context, brightnessValue)
        }
    }

    // Check whether this app has android write settings permission.
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasWriteSettingsPermission(context: Context): Boolean {
        var canWrite = true
        canWrite = Settings.System.canWrite(context)
        return canWrite
    }

    // Start can modify system settings panel to let user change the write settings permission.
    private fun changeWriteSettingsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        context.startActivity(intent)
    }

    // This function only take effect in real physical android device, it can not take effect in android emulator.
    private fun changeScreenBrightness(context: Context, screenBrightnessValue: Int) {   // Change the screen brightness change mode to manual.
        Log.d("BrightnessValue", "Set: $screenBrightnessValue")
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue)

       // val brightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    }
}