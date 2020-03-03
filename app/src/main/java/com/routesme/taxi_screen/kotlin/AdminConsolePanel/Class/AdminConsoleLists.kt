package com.routesme.taxi_screen.kotlin.AdminConsolePanel.Class

import android.app.Activity
import com.routesme.taxi_screen.kotlin.Model.*
import com.routesme.taxiscreen.BuildConfig

class AdminConsoleLists(val activity: Activity) {

    companion object {
        private const val defaultValue = "- -"
    }

    private val sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE)
    val MASTER_ITEMS = listOf(
            MasterItem(0, MasterItemType.Info),
            MasterItem(1, MasterItemType.Account),
            MasterItem(2, MasterItemType.Settings)
    )
    val INFO_CELLS = listOf(
            LabelCell("Vehicle"),
            DetailCell("Plate Number", "${plateNumber()}", true),
            DetailCell("Institution Name", "${institutionName()}", false),
            LabelCell("General"),
            DetailCell("Channel ID", "#${channelId()}", true),
            DetailCell("App Version", appVersion(), true),
            DetailCell("Sim Serial Number", "${simSerialNumber()}", true),
            DetailCell("Device Serial Number", "${deviceSerialNumber()}", false)
    )
    val ACCOUNT_CELLS = listOf(
            LabelCell("Technician"),
            DetailCell("User Name", "${technicalUserName()?.capitalize()}", true),
            DetailCell("Registration Date", "${registrationDate()}", false),
            ActionCell("Log off")
    )
    val SETTINGS_CELLS = listOf(
            LabelCell("Launcher"),
            DetailActionCell("Home App", DetailActionStatus.PENDING, "Open launcher settings"),
            LabelCell("Tracking"),
            DetailActionCell("Location", DetailActionStatus.PENDING, "Open general settings")
    )

    private fun plateNumber() = sharedPreferences?.getString("taxiPlateNumber", defaultValue)
    private fun institutionName() = sharedPreferences?.getString("institutionName", defaultValue)
    private fun channelId() = if (getChannelId() != -999) { getChannelId() } else { defaultValue }
    private fun getChannelId() = sharedPreferences?.getInt("tabletChannelId", -999)
    private fun appVersion() = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
    private fun simSerialNumber() = sharedPreferences?.getString("simCardNumber", defaultValue)
    private fun deviceSerialNumber() = sharedPreferences?.getString("tabletSerialNo", defaultValue)
    private fun technicalUserName() = sharedPreferences?.getString("technicalUserName", defaultValue)
    private fun registrationDate() = sharedPreferences?.getString("registrationDate", defaultValue)
}