package com.routesme.taxi_screen.kotlin.LocationTrackingService.Class

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi_screen.kotlin.Model.LocationJsonObject
import com.routesme.taxi_screen.kotlin.Model.VehicleLocation
import org.json.JSONException
import tech.gusavila92.websocketclient.WebSocketClient
import java.text.SimpleDateFormat


class TrackingDataLayer(private var trackingWebSocket: WebSocketClient) {
    private val db = TrackingDatabase(App.instance).trackingDao()

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa")

   // private lateinit var results: List<List<VehicleLocation>>
   // private lateinit var filteredResults: List<VehicleLocation>

     fun executeTrackingLogic() {
       val databaseFeeds = getDatabaseFeeds()
         if (!databaseFeeds.isNullOrEmpty()){

             val filteredResult = getMajorFeeds(databaseFeeds)
             // Log.d("\n\n Tracking-Logic", "filteredResult: $filteredResult")

             sendLocationViaSocket(filteredResult)
         }
    }

    private fun getMajorFeeds(databaseFeeds: List<MutableList<VehicleLocation>>): Set<VehicleLocation> {
        val majorFeeds = mutableSetOf<VehicleLocation>()
        for (feeds in databaseFeeds){
            for (currentIndex in feeds.indices){
               val nextIndex = currentIndex + 1
                if (nextIndex <= feeds.lastIndex){
                    val currentLocation = feeds[currentIndex].location
                    val nextLocation = feeds[nextIndex].location
                    val distance = distance(currentLocation, nextLocation)
                    if (distance >= 10){
                        majorFeeds.apply {
                            add(feeds[currentIndex])
                            add(feeds[nextIndex])
                        }
                    }else{
                        feeds.removeAt(nextIndex)
                        currentIndex.dec()
                    }
                }
            }
        }
        return majorFeeds
    }
/*
    fun sendOfflineTrackingLocationsToServer() {
        results = getLocationFeeds()
        filteredResults = getFilteredFeeds(results)
        Log.d("OfflineFeeds", "$filteredResults")

        /*
        if (!db.loadAllLocations().isNullOrEmpty()) {
                sendLocationViaSocket(db.loadAllLocations())
            clearTrackingTable()
        } else {
            Log.d("trackingWebSocketKotlin", "No offline location is exists!")
        }
        */
    }
    */

    private fun getFilteredFeeds(results: List<List<VehicleLocation>>): List<VehicleLocation> {
        return mutableListOf<VehicleLocation>().apply {
            for (r in results){
                add(r[r.lastIndex])
            }
        }
    }

    private fun getDatabaseFeeds(): List<MutableList<VehicleLocation>> {
        val feedsList1 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(0,29.3759,47.9774,1597048475))  //29.376646, 47.985642
            add(VehicleLocation(1,29.376612, 47.985659,1597048477))
            add(VehicleLocation(2,29.376593, 47.985671,1597048479))
            add(VehicleLocation(3,29.376573, 47.985680,1597048480))
            add(VehicleLocation(4,29.377115, 47.993431,1597048482))
        }
        val feedsList2 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(5,29.376502, 47.985689,1597048484))
            add(VehicleLocation(6,29.376463, 47.985702,1597048486))
            add(VehicleLocation(7,29.376419, 47.985723,1597048487))
        }
        val feedsList3 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(8,29.376396, 47.985732,1597048489))
            add(VehicleLocation(9,29.376314, 47.985758,1597048490))
            add(VehicleLocation(10,29.376156, 47.985822,1597048492))
            add(VehicleLocation(11,29.376116, 47.985842,1597048494))
        }
        val feedsList4 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(12,29.376076, 47.986028,1597048495))
            add(VehicleLocation(13,29.376050, 47.986031,1597048497))
            add(VehicleLocation(14,29.376005, 47.986088,1597048499))
            add(VehicleLocation(15,29.375981, 47.986101,1597048500))
            add(VehicleLocation(16,29.375846, 47.985987,1597048503))
            add(VehicleLocation(17,29.375576, 47.986235,1597048506))
            add(VehicleLocation(18,29.375544, 47.986261,1597048509))
        }
        val feedsList5 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(19,29.375499, 47.986304,1597048511))
            add(VehicleLocation(20,29.375248, 47.986658,1597048519))
        }
        val feedsList6 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(21,29.375164, 47.987026,1597048495))
            add(VehicleLocation(22,29.375243, 47.987125,1597048497))
            add(VehicleLocation(23,29.375450, 47.987364,1597048499))
            add(VehicleLocation(24,29.375568, 47.987519,1597048500))
            add(VehicleLocation(25,29.375638, 47.987590,1597048482))
        }
        val feedsList7 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(26,29.375711, 47.987677,1597048499))
            add(VehicleLocation(27,29.375744, 47.987710,1597048500))
            add(VehicleLocation(28,29.375835, 47.987819,1597048503))
            add(VehicleLocation(29,29.375950, 47.987955,1597048511))
            add(VehicleLocation(30,29.375994, 47.988004,1597048519))
        }
        val feedsList8 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(31,29.376167, 47.988231,1597048511))
            add(VehicleLocation(32,29.376295, 47.988398,1597048519))
            add(VehicleLocation(33,29.376346, 47.988455,1597048499))
            add(VehicleLocation(34,29.376423, 47.988549,1597048500))
            add(VehicleLocation(35,29.376610, 47.988795,1597048503))
        }
        val feedsList9 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(36,29.376652, 47.988847,1597048500))
            add(VehicleLocation(37,29.376782, 47.989011,1597048503))
            add(VehicleLocation(38,29.376829, 47.989078,1597048511))
            add(VehicleLocation(39,29.376891, 47.989146,1597048519))
        }
        val feedsList10 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(40,29.376908, 47.989169,1597048511))
            add(VehicleLocation(41,29.376922, 47.989189,1597048519))
            add(VehicleLocation(42,29.376939, 47.989206,1597048500))
            add(VehicleLocation(43,29.376958, 47.989229,1597048503))
        }
        val feedsList11 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(44,29.376994, 47.989461,1597048511))
            add(VehicleLocation(45,29.377157, 47.989493,1597048500))
            add(VehicleLocation(46,29.377335, 47.989743,1597048503))
            add(VehicleLocation(47,29.377435, 47.989879,1597048519))
        }
        val feedsList12 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(48,29.377544, 47.990016,1597048511))
            add(VehicleLocation(49,29.377708, 47.990240,1597048484))
            add(VehicleLocation(50,29.377840, 47.990423,1597048486))
            add(VehicleLocation(51,29.377854, 47.990438,1597048487))
            add(VehicleLocation(52,29.377860, 47.990444,1597048519))
        }
        val feedsList13 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(53,29.377926, 47.990530,1597048511))
            add(VehicleLocation(54,29.377982, 47.990611,1597048484))
            add(VehicleLocation(55,29.378006, 47.990642,1597048486))
            add(VehicleLocation(56,29.378035, 47.990678,1597048487))
            add(VehicleLocation(57,29.378064, 47.990725,1597048519))
            add(VehicleLocation(58,29.378064, 47.990719,1597048484))
            add(VehicleLocation(59,29.378091, 47.990762,1597048486))
            add(VehicleLocation(60,29.378128, 47.990807,1597048487))
        }
        val feedsList14 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(61,29.378154, 47.990837,1597048484))
            add(VehicleLocation(62,29.378269, 47.990990,1597048486))
            add(VehicleLocation(63,29.378467, 47.991241,1597048487))
            add(VehicleLocation(64,29.377878, 47.991564,1597048511))
            add(VehicleLocation(65,29.377770, 47.991732,1597048519))
        }
        val feedsList15 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(66,29.377832, 47.991459,1597048484))
            add(VehicleLocation(67,29.377720, 47.991132,1597048486))
            add(VehicleLocation(68,29.377512, 47.991306,1597048487))
            add(VehicleLocation(69,29.377440, 47.991376,1597048511))
            add(VehicleLocation(70,29.377336, 47.991508,1597048519))
            add(VehicleLocation(71,29.377115, 47.991739,1597048484))
            add(VehicleLocation(72,29.377042, 47.991792,1597048486))
            add(VehicleLocation(73,29.377010, 47.991837,1597048487))
        }
        val feedsList16 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(74,29.376842, 47.991994,1597048511))
            add(VehicleLocation(75,29.376726, 47.992109,1597048484))
            add(VehicleLocation(76,29.376725, 47.992160,1597048486))
            add(VehicleLocation(77,29.376736, 47.992171,1597048487))
            add(VehicleLocation(78,29.376745, 47.992175,1597048519))
        }
        val feedsList17 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(79,29.376759, 47.992187,1597048484))
            add(VehicleLocation(80,29.376800, 47.992244,1597048486))
            add(VehicleLocation(81,29.376879, 47.992342,1597048511))
            add(VehicleLocation(82,29.376930, 47.992399,1597048519))
            add(VehicleLocation(83,29.376957, 47.992439,1597048486))
            add(VehicleLocation(84,29.377097, 47.993059,1597048487))
        }

        return mutableListOf<MutableList<VehicleLocation>>().apply {

            add(feedsList1)
            add(feedsList2)
            add(feedsList3)
            add(feedsList4)
            add(feedsList5)
            add(feedsList6)
            add(feedsList7)
            add(feedsList8)
            add(feedsList9)
            add(feedsList10)
            add(feedsList11)
            add(feedsList12)
            add(feedsList13)
            add(feedsList14)
            add(feedsList15)
            add(feedsList16)
            add(feedsList17)
        }
    }

    fun insertLocation(location: Location) {
        val lastLocation = db.loadLastLocation()
        if (lastLocation == null || (location.latitude != lastLocation.latitude && location.longitude != lastLocation.longitude)){
            val currentLocation = VehicleLocation(0, location.latitude, location.longitude, System.currentTimeMillis()/1000)
            db.insertLocation(currentLocation)
            Log.d("trackingWebSocketKotlin", "Insert new location:  $currentLocation")
        }
    }

    fun locationChecker() {
        if (!db.loadAllLocations().isNullOrEmpty()) {
            val firstLocation = db.loadFirstLocation().location
            val lastLocation = db.loadLastLocation().location
            if (firstLocation != lastLocation) {
                if (distance(firstLocation, lastLocation) >= 2) {
                    val vehicleLocation= mutableSetOf<VehicleLocation>().apply {
                        add(db.loadLastLocation())
                    }
                    sendLocationViaSocket(vehicleLocation)
                    clearTrackingTable()
                }
            }
        }
    }

    private fun distance(firstLocation: Location, lastLocation: Location) = firstLocation.distanceTo(lastLocation)

    private fun clearTrackingTable() {
        db.clearTrackingData()
        Log.d("trackingWebSocketKotlin", "Clear tracking table!")
    }

    private fun sendLocationViaSocket(vehicleLocations: Set<VehicleLocation>){
        val locationJsonArray = JsonArray()
        for (l in vehicleLocations){
            val locationJsonObject: JsonObject = LocationJsonObject(l).toJSON()
            locationJsonArray.add(locationJsonObject)
        }

        val sendLocationObject = JsonObject()

        try { // Add the JSONArray to the JSONObject
            sendLocationObject.add("SendLocation", locationJsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

       // Log.i("trackingWebSocket:  ", "Send-> $sendLocationObject")
        trackingWebSocket.send(sendLocationObject.toString())
    }
}