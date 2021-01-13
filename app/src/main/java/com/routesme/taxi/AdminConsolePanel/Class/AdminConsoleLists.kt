package com.routesme.taxi.AdminConsolePanel.Class

import android.app.Activity
import com.routesme.taxi.AdminConsolePanel.Model.*
import com.routesme.taxi.utils.Session

class AdminConsoleLists(val activity: Activity) {

    private val session = Session(activity)
    private val adminConsoleHelper = AdminConsoleHelper(activity)
    val masterItems = listOf(
            MasterItem(0, MasterItemType.Info),
            MasterItem(1, MasterItemType.Account),
            MasterItem(2, MasterItemType.Settings),
            MasterItem(3, MasterItemType.Location_Feeds)
    )
    val infoCells = listOf(
            LabelCell("Vehicle"),
            DetailCell("Plate Number", "${session.plateNumber()}", true),
            DetailCell("Institution Name", "${session.institutionName()}", false),
            LabelCell("General"),
           // DetailCell("Channel ID", "#${adminConsoleHelper.channelId()}", true),
            DetailCell("App Version", session.appVersion(), true),
            DetailCell("Sim Serial Number", "${session.simSerialNumber()}", true),
            DetailCell("Device Serial Number", "${session.deviceSerialNumber()}", false)
    )
    val accountCells = listOf(
            LabelCell("Technician"),
            DetailCell("User Name", "${session.technicalUserName()?.capitalize()}", true),
            DetailCell("Registration Date", "${session.registrationDate()}", false),
            ActionCell(Actions.LogOff.title)
    )
    val settingsCells = listOf(
            LabelCell("Launcher"),
            DetailActionCell("Home App", adminConsoleHelper.isMyAppDefaultLauncher(), Actions.Launcher.title),
            LabelCell("Tracking"),
            DetailActionCell("Location", adminConsoleHelper.isLocationPermissionsAllowed(), Actions.General.title)
    )
}