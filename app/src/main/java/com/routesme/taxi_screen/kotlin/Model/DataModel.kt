package com.routesme.taxi_screen.kotlin.Model

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Server RequestHeaders
data class RequestHeaders(val Authorization: String? = null, val country_code: String? = null, val app_version: String? = null)

//Advertisement
data class BannerModel(@SerializedName("Adv_ID") val advertisement_ID: Int = 0, @SerializedName("Adv_URL") val advertisement_URL: String? = null)

data class VideoModel(@SerializedName("Video_ID") val advertisement_ID: Int = 0, @SerializedName("Video_URL") val advertisement_URL: String? = null)
data class ItemAnalytics(val id: Int = 0, val name: String = "item_name")

//Authentication
data class AuthCredentials(var Username: String = "", var Password: String = "")

data class Token(val access_token: String? = null)
data class AuthCredentialsError(val ErrorNumber: Int = 0, @SerializedName("ErrorMasseg") val ErrorMessage: String? = null)
data class Authorization(var isAuthorized: Boolean, var responseCode: Int) : Serializable

//Tablet registration
data class TabletCredentials(@SerializedName("tabletRegesterTaxiOfficeID") var taxiOfficeId: Int = 0, @SerializedName("tabletRegesterCarPlateNo") var taxiPlateNumber: String? = null, @SerializedName("tabletRegesterSerialNo") var DeviceId: String? = null, @SerializedName("SimSerialNumber") var SimSerialNumber: String? = null)

//Offices list
data class TaxiOfficeList(@SerializedName("data") val officesData: List<Office?>? = null, @SerializedName("included") val officesIncluded: IncludedOffices? = null)

data class IncludedOffices(val recentOffices: List<Office?>? = null)
data class Office(val taxiOfficeID: Int? = null, val taxiOfficeCarsCount: Int? = null, val taxiOfficeName: String? = null, val taxiOfficePhoneNumber: String? = null)
//Office plates list
data class OfficePlatesList(@SerializedName("data") val officePlatesData: List<TaxiPlate?>? = null)

data class TaxiPlate(val tabletCarPlateNo: String? = null)
data class ItemType(val itemName: String? = null, val isHeader: Boolean = false, val isNormalItem: Boolean = false, val officeId: Int = 0)
//Tablet Data
data class TabletInfo(@SerializedName("tabletRegesterPassword") val tabletPassword: String? = null, @SerializedName("tabletRegesterChannelID") val tabletChannelId: Int = 0)

//Tracking Location Service
@Entity(tableName = "Tracking")
data class VehicleLocation(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo(name = "latitude") var latitude: Double, @ColumnInfo(name = "longitude") var longitude: Double, @ColumnInfo(name = "timestamp") var timestamp: String){
    val location: Location
    get() {
        val location = Location("provider")
        location.latitude = latitude
        location.longitude = longitude
        return location
    }
}
//Admin Console...
data class MasterItem(val id: Int, val type: MasterItemType)

enum class MasterItemType { Info, Account, Settings }

interface ICell
class LabelCell(val title: String) : ICell
class DetailCell(val title: String, val value: String, val splitLine: Boolean = false) : ICell
class ActionCell(val action: String) : ICell
class DetailActionCell(val title: String, val status: DetailActionStatus, val action: String) : ICell

enum class DetailActionStatus{DONE, PENDING}
enum class Actions(val title: String) {Launcher("Open launcher settings"), General("Open general settings"), LogOff("Log off")}

//ThemeMode Interface
interface IModeChanging {
    fun onModeChange()
}

//Payment Service...
data class PaymentData(var driverToken:String = "", var paymentAmount: Double = 0.0) : Serializable

data class PaymentMessage(var identifier: String? = null, var udid: String? = null, var amount: Double? = null, var status: String? = null) : Serializable
data class PaymentProgressMessage(var identifier: String? = null, var status: String? = null) : Serializable
enum class PaymentStatus(val text: String){Initiate("initiate"), Cancel("cancel"), Paid("paid"), Timeout("timeout")}
/*
Identifier: userid + timestamp,
udid: token,
amount: 3
Status: initiate
 */