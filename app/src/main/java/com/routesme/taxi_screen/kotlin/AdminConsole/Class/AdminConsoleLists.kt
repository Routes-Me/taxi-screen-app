package com.routesme.taxi_screen.kotlin.AdminConsole.Class

import android.app.Activity
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Model.*

class AdminConsoleLists {
    object List {
        val MASTER_ITEMS = listOf(
                MasterItem(0, MasterItemType.Info),
                MasterItem(1, MasterItemType.Account),
                MasterItem(2, MasterItemType.Settings)
        )
        val INFO_CELLS = listOf(
                LabelCell("Vehicle"),
                DetailCell("Plate Number", "32225", true),
                DetailCell("Institution Name", "Afnan", false),
                LabelCell("General"),
                DetailCell("Channel ID", "#1", true),
                DetailCell("App Version", "1.0.1", true),
                DetailCell("Sim Serial Number", "12324ddqwe24324321", true),
                DetailCell("Device Serial Number", "WED213135656TG767HHF", false)
        )
        val ACCOUNT_CELLS = listOf(
                LabelCell("Technician"),
                DetailCell("User Name", "latifa", true),
                DetailCell("Registration Date", "12, Jan 1:00 PM", false),
                ActionCell("Log off")
        )
        val SETTINGS_CELLS = listOf(
                LabelCell("Launcher"),
                DetailActionCell("Home App", "DONE", false),
                LabelCell("Tracking"),
                DetailActionCell("Location", "OPEN SETTINGS", false)
        )
    }
}