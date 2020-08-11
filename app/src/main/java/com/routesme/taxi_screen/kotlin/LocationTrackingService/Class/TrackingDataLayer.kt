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
       val results = getDatabaseFeeds()
         if (!results.isNullOrEmpty()){
             Log.d("Tracking-Logic", "DatabaseFeeds-Result: $results")
         }
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

    private fun getDatabaseFeeds(): List<List<VehicleLocation>> {
        val feedsList1 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(0,29.256468333333334,47.94098999999999,1597048475))
            add(VehicleLocation(1,29.245983333333335,47.930003333333325,1597048477))
            add(VehicleLocation(2,29.223214999999996,47.95335,1597048479))
            add(VehicleLocation(3,29.194446666666667,47.98218833333333,1597048480))
            add(VehicleLocation(4,29.158475,48.00690833333333,1597048482))
        }
        val feedsList2 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(5,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(6,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(7,29.063689999999997,47.983561666666674,1597048487))
        }
        val feedsList3 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(8,29.02527166666666,47.99042833333333,1597048489))
            add(VehicleLocation(9,28.989240000000002,48.001414999999994,1597048490))
            add(VehicleLocation(10,28.944785000000003,48.013775,1597048492))
            add(VehicleLocation(11,28.906321666666667,48.046733333333336,1597048494))
        }
        val feedsList4 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(12,28.865439999999996,48.04398666666666,1597048495))
            add(VehicleLocation(13,28.822135000000003,48.0536,1597048497))
            add(VehicleLocation(14,28.794458333333335,48.046733333333336,1597048499))
            add(VehicleLocation(15,28.772793333333332,48.015148333333336,1597048500))
            add(VehicleLocation(16,28.755938333333333,47.986308333333334,1597048503))
            add(VehicleLocation(17,28.737878333333334,47.95609666666666,1597048506))
            add(VehicleLocation(18,28.721000000000003,47.93689166666666,1597048509))
        }
        val feedsList5 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(19,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(20,28.625814999999996,47.91903833333334,1597048519))
        }
        val feedsList6 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(21,28.865439999999996,48.04398666666666,1597048495))
            add(VehicleLocation(22,28.822135000000003,48.0536,1597048497))
            add(VehicleLocation(23,28.794458333333335,48.046733333333336,1597048499))
            add(VehicleLocation(24,28.772793333333332,48.015148333333336,1597048500))
            add(VehicleLocation(25,29.158475,48.00690833333333,1597048482))
        }
        val feedsList7 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(26,28.794458333333335,48.046733333333336,1597048499))
            add(VehicleLocation(27,28.772793333333332,48.015148333333336,1597048500))
            add(VehicleLocation(28,28.755938333333333,47.986308333333334,1597048503))
            add(VehicleLocation(29,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(30,28.625814999999996,47.91903833333334,1597048519))
        }
        val feedsList8 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(31,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(32,28.625814999999996,47.91903833333334,1597048519))
            add(VehicleLocation(33,28.794458333333335,48.046733333333336,1597048499))
            add(VehicleLocation(34,28.772793333333332,48.015148333333336,1597048500))
            add(VehicleLocation(35,28.755938333333333,47.986308333333334,1597048503))
        }
        val feedsList9 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(36,28.772793333333332,48.015148333333336,1597048500))
            add(VehicleLocation(37,28.755938333333333,47.986308333333334,1597048503))
            add(VehicleLocation(38,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(39,28.625814999999996,47.91903833333334,1597048519))
        }
        val feedsList10 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(40,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(41,28.625814999999996,47.91903833333334,1597048519))
            add(VehicleLocation(42,28.772793333333332,48.015148333333336,1597048500))
            add(VehicleLocation(43,28.755938333333333,47.986308333333334,1597048503))
        }
        val feedsList11 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(44,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(45,28.772793333333332,48.015148333333336,1597048500))
            add(VehicleLocation(46,28.755938333333333,47.986308333333334,1597048503))
            add(VehicleLocation(47,28.625814999999996,47.91903833333334,1597048519))
        }
        val feedsList12 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(48,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(49,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(50,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(51,29.063689999999997,47.983561666666674,1597048487))
            add(VehicleLocation(52,28.625814999999996,47.91903833333334,1597048519))
        }
        val feedsList13 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(53,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(54,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(55,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(56,29.063689999999997,47.983561666666674,1597048487))
            add(VehicleLocation(57,28.625814999999996,47.91903833333334,1597048519))
            add(VehicleLocation(58,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(59,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(60,29.063689999999997,47.983561666666674,1597048487))
        }
        val feedsList14 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(61,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(62,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(63,29.063689999999997,47.983561666666674,1597048487))
            add(VehicleLocation(64,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(65,28.625814999999996,47.91903833333334,1597048519))
        }
        val feedsList15 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(66,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(67,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(68,29.063689999999997,47.983561666666674,1597048487))
            add(VehicleLocation(69,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(70,28.625814999999996,47.91903833333334,1597048519))
            add(VehicleLocation(71,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(72,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(73,29.063689999999997,47.983561666666674,1597048487))
        }
        val feedsList16 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(74,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(75,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(76,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(77,29.063689999999997,47.983561666666674,1597048487))
            add(VehicleLocation(78,28.625814999999996,47.91903833333334,1597048519))
        }
        val feedsList17 = mutableListOf<VehicleLocation>().apply {
            add(VehicleLocation(79,29.122491666666665,47.99180166666667,1597048484))
            add(VehicleLocation(80,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(81,28.69811666666667,47.90393166666666,1597048511))
            add(VehicleLocation(82,28.625814999999996,47.91903833333334,1597048519))
            add(VehicleLocation(83,29.093695000000004,47.998668333333335,1597048486))
            add(VehicleLocation(84,29.063689999999997,47.983561666666674,1597048487))
        }

        return mutableListOf<List<VehicleLocation>>().apply {

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
                    val vehicleLocation= mutableListOf<VehicleLocation>().apply {
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

    private fun sendLocationViaSocket(vehicleLocations: List<VehicleLocation>){
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

        Log.i("trackingWebSocket:  ", "Send-> $sendLocationObject")
        trackingWebSocket.send(sendLocationObject.toString())
    }
}