package com.routesme.vehicles.data.model

data class MasterItem(val id: Int, val type: String)
enum class MasterItemType(val title: String) { Info("Info"), Account("Account"), Settings("Settings"), RoutesAndTickets("Routes & Tickets ") , Location_Feeds("Location Feeds") }

interface ICell
class LabelCell(val title: String) : ICell
class DetailCell(val title: String, val value: String, val splitLine: Boolean = false) : ICell
class ActionCell(val action: String, val textColor: ActionCellTextColor) : ICell
class DetailActionCell(val title: String, val status: DetailActionStatus, val action: String) : ICell

enum class ActionCellTextColor(val colorCode: String){Red("#d50000"), Blue("#18428f")}
enum class DetailActionStatus { DONE, PENDING }
enum class Actions(val title: String) { Launcher("Open launcher settings"), General("Open general settings"), LogOff("Unlink Device"), SyncAndUpdateCarrierInformation ("Sync and Update") }