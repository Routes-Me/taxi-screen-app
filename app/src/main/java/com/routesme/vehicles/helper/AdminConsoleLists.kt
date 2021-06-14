package com.routesme.vehicles.helper

import android.app.Activity
import com.routesme.vehicles.BuildConfig
import com.routesme.vehicles.data.model.*
import com.routesme.vehicles.uplevels.CarrierInformation

class AdminConsoleLists(val activity: Activity) {

    private val adminConsoleHelper = AdminConsoleHelper(activity)

    val masterItems = mutableListOf<MasterItem>().apply{
        add(MasterItem(0, MasterItemType.Info.title))
        add(MasterItem(1, MasterItemType.Account.title))
        add(MasterItem(2, MasterItemType.Settings.title))
        if (BuildConfig.FLAVOR == "bus") add(MasterItem(3, MasterItemType.RoutesAndTickets.title))
    }

    val infoCells = mutableListOf<ICell>().apply {
        add(LabelCell("Vehicle"))
        add(DetailCell("Plate Number", "${adminConsoleHelper.plateNumber()}", true))
        add(DetailCell("Institution Name", "${adminConsoleHelper.institutionName()}", false))
        add(LabelCell("General"))
        // DetailCell("Channel ID", "#${adminConsoleHelper.channelId()}", true),
        add(DetailCell("App Version", adminConsoleHelper.appVersion(), true))
        adminConsoleHelper.getNetworkType()?.let { addAll(it) }
        adminConsoleHelper.getSimStatus()?.let { addAll(it) }
        add(DetailCell("Sim Serial Number", "${adminConsoleHelper.simSerialNumber()}", true))
        add(DetailCell("IMEI", "${adminConsoleHelper.imei()}", true))
        // DetailCell("Device Serial Number", "${adminConsoleHelper.deviceSerialNumber()}", false)
        adminConsoleHelper.getBuildInfo()?.let { addAll(it) }
    }.toList()
    val accountCells = listOf(
            LabelCell("Technician"),
            DetailCell("User Name", "${adminConsoleHelper.technicalUserName()?.capitalize()}", true),
            DetailCell("Registration Date", "${adminConsoleHelper.registrationDate()}", false),
            ActionCell(Actions.LogOff.title, ActionCellTextColor.Red)
    )
    val settingsCells = listOf(
            LabelCell("Launcher"),
            DetailActionCell("Home App", adminConsoleHelper.isMyAppDefaultLauncher(), Actions.Launcher.title),
            LabelCell("Tracking"),
            DetailActionCell("Location", adminConsoleHelper.isLocationPermissionsAllowed(), Actions.General.title),
            LabelCell("System Log"),
            DetailActionCell("Logs", DetailActionStatus.DONE, Actions.SystemLogs.title)
    )

    val routesAndTicketsCells = mutableListOf<ICell>().apply {
        add(LabelCell("Route"))
        add(DetailCell("Route Number", "${CarrierInformation().routeNumber}", true))
        add(DetailCell("Last Update", "${CarrierInformation().lastUpdateDate}", true))
        add(ActionCell(Actions.SyncAndUpdateCarrierInformation.title, ActionCellTextColor.Blue))
        CarrierInformation().tickets?.let {
            add(LabelCell("Tickets"))
            for (ticket in it){
                add(DetailCell("${ticket.amount} files", "1 day", true))
            }
        }
    }
}