package com.routesme.taxi_screen.kotlin.View.AdminConsole.Model

object AdminConsoleLists {
    val MASTER_ITEMS = listOf(
            MasterItem(0, ItemType.Info),
            MasterItem(1, ItemType.Account),
            MasterItem(2, ItemType.Settings)
    )
    val INFO_CELLS = listOf(
            DetailCell("Vehicle", cellType = CellType.Label),
            DetailCell("Plate Number", "32225", true, CellType.Detail),
            DetailCell("Institution Name", "Afnan", false, CellType.Detail),
            DetailCell("General", cellType = CellType.Label),
            DetailCell("Channel ID", "#1", true, CellType.Detail),
            DetailCell("App Version", "1.0.1", true, CellType.Detail),
            DetailCell("Sim Serial Number", "12324ddqwe24324321", true, CellType.Detail),
            DetailCell("Device Serial Number", "WED213135656TG767HHF", false, CellType.Detail)
    )
    val ACCOUNT_CELLS = listOf(
            DetailCell("Technician", cellType = CellType.Label),
            DetailCell("User Name", "latifa", true, CellType.Detail),
            DetailCell("Registration Date", "12, Jan 1:00 PM", false, CellType.Detail),
            DetailCell("Log off", cellType = CellType.Action)
    )
    val SETTINGS_CELLS = listOf(
            DetailCell("Launcher", cellType = CellType.Label),
            DetailCell("Home App", "DONE", false, CellType.DetailAction),
            DetailCell("Tracking", cellType = CellType.Label),
            DetailCell("Location", "OPEN SETTINGS", false, CellType.DetailAction)
    )

    data class MasterItem(val id: Int, val type: ItemType)
    enum class ItemType { Info, Account, Settings }
    data class DetailCell(val title: String, val value: String = "", val splitLine: Boolean = false, val cellType: CellType)
    enum class CellType { Label, Detail, Action, DetailAction }
}