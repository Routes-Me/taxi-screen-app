package com.routesme.taxi_screen.kotlin.Model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Server RequestHeaders
data class RequestHeaders(  val Authorization: String? = null, val country_code: String? = null, val app_version: String? = null)



//Advertisement
data class BannerModel(@SerializedName("Adv_ID")val advertisement_ID: Int = 0, @SerializedName("Adv_URL")val advertisement_URL: String? = null)
data class VideoModel(@SerializedName("Video_ID")val advertisement_ID: Int = 0,@SerializedName("Video_URL") val advertisement_URL: String? = null)
data class ItemAnalytics( val id: Int = 0, val name: String? = null)

//Authentication
data class AuthCredentials(var Username: String = "", var Password: String = "")
data class Token(val access_token: String? = null)
data class AuthCredentialsError (val ErrorNumber: Int = 0, @SerializedName("ErrorMasseg") val ErrorMessage: String? = null)
data class Authorization(var isAuthorized:Boolean , var responseCode:Int) : Serializable

//Tablet registration
data class TabletCredentials(@SerializedName("tabletRegesterTaxiOfficeID") var taxiOfficeId: Int = 0, @SerializedName("tabletRegesterCarPlateNo") var taxiPlateNumber: String? = null, @SerializedName("tabletRegesterSerialNo") var DeviceId: String? = null, @SerializedName("SimSerialNumber") var SimSerialNumber: String? = null)
//Offices list
data class TaxiOfficeList(@SerializedName("data")  val officesData: List<Office?>? = null, @SerializedName("included") val officesIncluded: IncludedOffices? = null)
data class IncludedOffices(val recentOffices: List<Office?>? = null)
data class Office(val taxiOfficeID: Int? = null, val taxiOfficeCarsCount: Int? = null , val taxiOfficeName: String? = null, val taxiOfficePhoneNumber: String? = null)
//Office plates list
data class OfficePlatesList(@SerializedName("data") val officePlatesData: List<TaxiPlate?>? = null)
data class TaxiPlate(val tabletCarPlateNo: String? = null)
data class ItemType(val itemName: String? = null , val isHeader:Boolean = false, val isNormalItem: Boolean = false , val officeId: Int = 0)
//Tablet Data
data class TabletInfo(@SerializedName("tabletRegesterPassword") val tabletPassword: String? = null, @SerializedName("tabletRegesterChannelID") val tabletChannelId: Int = 0)

//Tracking Location Service
@Entity(tableName = "Tracking")
data class Tracking(@PrimaryKey(autoGenerate = true) var id: Int = 0, @Embedded var location: TrackingLocation, @ColumnInfo(name = "timestamp") var timestamp: String)
data class TrackingLocation (val latitude:Double , val longitude:Double)

