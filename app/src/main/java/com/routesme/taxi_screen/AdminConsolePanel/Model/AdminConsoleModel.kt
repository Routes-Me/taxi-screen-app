package com.routesme.taxi_screen.AdminConsolePanel.Model

data class MasterItem(val id: Int, val type: MasterItemType)
enum class MasterItemType { Info, Account, Settings, Location_Feeds, Message_Feeds }

interface ICell
class LabelCell(val title: String) : ICell
class DetailCell(val title: String, val value: String, val splitLine: Boolean = false) : ICell
class ActionCell(val action: String) : ICell
class DetailActionCell(val title: String, val status: DetailActionStatus, val action: String) : ICell

enum class DetailActionStatus { DONE, PENDING }
enum class Actions(val title: String) { Launcher("Open launcher settings"), General("Open general settings"), LogOff("Log off") }