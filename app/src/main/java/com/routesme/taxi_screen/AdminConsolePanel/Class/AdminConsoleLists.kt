package com.routesme.taxi_screen.AdminConsolePanel.Class

import android.app.Activity
import com.routesme.taxi_screen.AdminConsolePanel.Model.*

class AdminConsoleLists(val activity: Activity) {

    private val adminConsoleHelper = AdminConsoleHelper(activity)

    val masterItems = listOf(
            MasterItem(0, MasterItemType.Info),
            MasterItem(1, MasterItemType.Account),
            MasterItem(2, MasterItemType.Settings),
            MasterItem(3, MasterItemType.Live_Tracking)
    )
    val infoCells = listOf(
            LabelCell("Vehicle"),
            DetailCell("Plate Number", "${adminConsoleHelper.plateNumber()}", true),
            DetailCell("Institution Name", "${adminConsoleHelper.institutionName()}", false),
            LabelCell("General"),
           // DetailCell("Channel ID", "#${adminConsoleHelper.channelId()}", true),
            DetailCell("App Version", adminConsoleHelper.appVersion(), true),
            DetailCell("Sim Serial Number", "${adminConsoleHelper.simSerialNumber()}", true),
            DetailCell("Device Serial Number", "${adminConsoleHelper.deviceSerialNumber()}", false)
    )
    val accountCells = listOf(
            LabelCell("Technician"),
            DetailCell("User Name", "${adminConsoleHelper.technicalUserName()?.capitalize()}", true),
            DetailCell("Registration Date", "${adminConsoleHelper.registrationDate()}", false),
            ActionCell(Actions.LogOff.title)
    )
    val settingsCells = listOf(
            LabelCell("Launcher"),
            DetailActionCell("Home App", adminConsoleHelper.isMyAppDefaultLauncher(), Actions.Launcher.title),
            LabelCell("Tracking"),
            DetailActionCell("Location", adminConsoleHelper.isLocationPermissionsAllowed(), Actions.General.title)
    )
}