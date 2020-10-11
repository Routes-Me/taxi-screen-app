package com.routesme.taxi_screen.LocationTrackingService.Class

import android.location.Location
import android.os.Handler
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi_screen.Class.App
import com.routesme.taxi_screen.LocationTrackingService.Database.TrackingDatabase
import com.routesme.taxi_screen.LocationTrackingService.Model.LocationFeed
import com.routesme.taxi_screen.LocationTrackingService.Model.LocationJsonObject
import com.routesme.taxi_screen.LocationTrackingService.Model.MessageFeed
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TrackingDataLayer( private val hubConnection: HubConnection) {
    private val trackingDatabase = TrackingDatabase.invoke(App.instance)
    private val locationFeedsDao = trackingDatabase.locationFeedsDao()
    private val messageFeedsDao = trackingDatabase.messageFeedsDao()
    private lateinit var sendFeedsHandler: Handler
    private lateinit var sendFeedsRunnable: Runnable

     fun executeTrackingLogic() {
         val savedLocationFeeds = locationFeedsDao.loadAllLocations()

         if (!savedLocationFeeds.isNullOrEmpty()){
             val lastFeedId = savedLocationFeeds.last().id
             val locationFeedGroups = savedLocationFeeds.groupBy { it.timestamp/5 }.values.map { it.toMutableList() }
             val filteredResult = getMajorFeeds(locationFeedGroups)
             val listOfFeeds = filteredResult.chunked(100)
             val listOfStringFeeds = listOfFeeds.map { MessageFeed(message = getJsonArray(it).toString()) }
             messageFeedsDao.insertFeeds(listOfStringFeeds)
             locationFeedsDao.clearLocationFeedsTable(lastFeedId)
             if (hubConnection.isConnected) {
                 sendFeedsHandlerSetup()
                 sendFeedsHandler.post(sendFeedsRunnable)
             }
         }
    }

    private fun sendFeedsHandlerSetup() {
        var i = 0
        val messageFeeds = messageFeedsDao.getAllMessages()
        sendFeedsRunnable = Runnable {
            if (hubConnection.isConnected) {
                if (i < messageFeeds.size) {
                    sendFeedsViaSocket(messageFeeds[i].message)
                    messageFeedsDao.delete(messageFeeds[i])
                    i++
                } else {
                    sendFeedsHandler.removeCallbacks(sendFeedsRunnable)
                }
                sendFeedsHandler.postDelayed(sendFeedsRunnable, 200)
            }else{
                sendFeedsHandler.removeCallbacks(sendFeedsRunnable)
            }
        }
        sendFeedsHandler = Handler()
    }

    private fun getJsonArray(locationFeeds: List<LocationFeed>):JsonArray {
        val locationJsonArray = JsonArray()
        for (l in locationFeeds){
            val locationJsonObject: JsonObject = LocationJsonObject(l).toJSON()
            locationJsonArray.add(locationJsonObject)
        }
        return locationJsonArray
    }

    private fun getMajorFeeds(feedGroups: List<MutableList<LocationFeed>>): Set<LocationFeed> {
        val majorFeeds = mutableSetOf<LocationFeed>()
        for (feeds in feedGroups){
            majorFeeds.addAll(filterFeeds(feeds))
        }
        return majorFeeds
    }

    private fun filterFeeds(feeds: MutableList<LocationFeed>, meters: Int = 10): MutableSet<LocationFeed> {
        val filteredFeeds = mutableSetOf<LocationFeed>()

        for (currentIndex in feeds.indices){
            val nextIndex = currentIndex + 1
            if (nextIndex <= feeds.lastIndex){
                val currentLocation = feeds[currentIndex].location
                val nextLocation = feeds[nextIndex].location
                val distance = distance(currentLocation, nextLocation)
                if (distance >= meters){
                    filteredFeeds.apply {
                        add(feeds[currentIndex])
                        add(feeds[nextIndex])
                    }
                }else{
                    feeds.removeAt(nextIndex)
                    currentIndex.dec()
                }
            }else {
                if (filteredFeeds.isEmpty()) {
                    filteredFeeds.add(feeds.first())
                }
            }
        }

        return filteredFeeds
    }

    private fun getDatabaseFeeds(): List<MutableList<LocationFeed>> {
        val feedsList1 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(0,29.3759,47.9774,1597048475))  //29.376646, 47.985642
            add(LocationFeed(1,29.376612, 47.985659,1597048477))
            add(LocationFeed(2,29.376593, 47.985671,1597048479))
            add(LocationFeed(3,29.376573, 47.985680,1597048480))
            add(LocationFeed(4,29.377115, 47.993431,1597048482))
        }
        val feedsList2 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(5,29.376502, 47.985689,1597048484))
            add(LocationFeed(6,29.376463, 47.985702,1597048486))
            add(LocationFeed(7,29.376419, 47.985723,1597048487))
        }
        val feedsList3 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(8,29.376396, 47.985732,1597048489))
            add(LocationFeed(9,29.376314, 47.985758,1597048490))
            add(LocationFeed(10,29.376156, 47.985822,1597048492))
            add(LocationFeed(11,29.376116, 47.985842,1597048494))
        }
        val feedsList4 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(12,29.376076, 47.986028,1597048495))
            add(LocationFeed(13,29.376050, 47.986031,1597048497))
            add(LocationFeed(14,29.376005, 47.986088,1597048499))
            add(LocationFeed(15,29.375981, 47.986101,1597048500))
            add(LocationFeed(16,29.375846, 47.985987,1597048503))
            add(LocationFeed(17,29.375576, 47.986235,1597048506))
            add(LocationFeed(18,29.375544, 47.986261,1597048509))
        }
        val feedsList5 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(19,29.375499, 47.986304,1597048511))
            add(LocationFeed(20,29.375248, 47.986658,1597048519))
        }
        val feedsList6 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(21,29.375164, 47.987026,1597048495))
            add(LocationFeed(22,29.375243, 47.987125,1597048497))
            add(LocationFeed(23,29.375450, 47.987364,1597048499))
            add(LocationFeed(24,29.375568, 47.987519,1597048500))
            add(LocationFeed(25,29.375638, 47.987590,1597048482))
        }
        val feedsList7 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(26,29.375711, 47.987677,1597048499))
            add(LocationFeed(27,29.375744, 47.987710,1597048500))
            add(LocationFeed(28,29.375835, 47.987819,1597048503))
            add(LocationFeed(29,29.375950, 47.987955,1597048511))
            add(LocationFeed(30,29.375994, 47.988004,1597048519))
        }
        val feedsList8 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(31,29.376167, 47.988231,1597048511))
            add(LocationFeed(32,29.376295, 47.988398,1597048519))
            add(LocationFeed(33,29.376346, 47.988455,1597048499))
            add(LocationFeed(34,29.376423, 47.988549,1597048500))
            add(LocationFeed(35,29.376610, 47.988795,1597048503))
        }
        val feedsList9 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(36,29.376652, 47.988847,1597048500))
            add(LocationFeed(37,29.376782, 47.989011,1597048503))
            add(LocationFeed(38,29.376829, 47.989078,1597048511))
            add(LocationFeed(39,29.376891, 47.989146,1597048519))
        }
        val feedsList10 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(40,29.376908, 47.989169,1597048511))
            add(LocationFeed(41,29.376922, 47.989189,1597048519))
            add(LocationFeed(42,29.376939, 47.989206,1597048500))
            add(LocationFeed(43,29.376958, 47.989229,1597048503))
        }
        val feedsList11 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(44,29.376994, 47.989461,1597048511))
            add(LocationFeed(45,29.377157, 47.989493,1597048500))
            add(LocationFeed(46,29.377335, 47.989743,1597048503))
            add(LocationFeed(47,29.377435, 47.989879,1597048519))
        }
        val feedsList12 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(48,29.377544, 47.990016,1597048511))
            add(LocationFeed(49,29.377708, 47.990240,1597048484))
            add(LocationFeed(50,29.377840, 47.990423,1597048486))
            add(LocationFeed(51,29.377854, 47.990438,1597048487))
            add(LocationFeed(52,29.377860, 47.990444,1597048519))
        }
        val feedsList13 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(53,29.377926, 47.990530,1597048511))
            add(LocationFeed(54,29.377982, 47.990611,1597048484))
            add(LocationFeed(55,29.378006, 47.990642,1597048486))
            add(LocationFeed(56,29.378035, 47.990678,1597048487))
            add(LocationFeed(57,29.378064, 47.990725,1597048519))
            add(LocationFeed(58,29.378064, 47.990719,1597048484))
            add(LocationFeed(59,29.378091, 47.990762,1597048486))
            add(LocationFeed(60,29.378128, 47.990807,1597048487))
        }
        val feedsList14 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(61,29.378154, 47.990837,1597048484))
            add(LocationFeed(62,29.378269, 47.990990,1597048486))
            add(LocationFeed(63,29.378467, 47.991241,1597048487))
            add(LocationFeed(64,29.377878, 47.991564,1597048511))
            add(LocationFeed(65,29.377770, 47.991732,1597048519))
        }
        val feedsList15 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(66,29.377832, 47.991459,1597048484))
            add(LocationFeed(67,29.377720, 47.991132,1597048486))
            add(LocationFeed(68,29.377512, 47.991306,1597048487))
            add(LocationFeed(69,29.377440, 47.991376,1597048511))
            add(LocationFeed(70,29.377336, 47.991508,1597048519))
            add(LocationFeed(71,29.377115, 47.991739,1597048484))
            add(LocationFeed(72,29.377042, 47.991792,1597048486))
            add(LocationFeed(73,29.377010, 47.991837,1597048487))
        }
        val feedsList16 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(74,29.376842, 47.991994,1597048511))
            add(LocationFeed(75,29.376726, 47.992109,1597048484))
            add(LocationFeed(76,29.376725, 47.992160,1597048486))
            add(LocationFeed(77,29.376736, 47.992171,1597048487))
            add(LocationFeed(78,29.376745, 47.992175,1597048519))
        }
        val feedsList17 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(79,29.376759, 47.992187,1597048484))
            add(LocationFeed(80,29.376800, 47.992244,1597048486))
            add(LocationFeed(81,29.376879, 47.992342,1597048511))
            add(LocationFeed(82,29.376930, 47.992399,1597048519))
            add(LocationFeed(83,29.376957, 47.992439,1597048486))
            add(LocationFeed(84,29.377097, 47.993059,1597048487))
        }
        val feedsList18 = mutableListOf<LocationFeed>().apply {
            add(LocationFeed(85,29.375576, 47.986235,1597048506))
            add(LocationFeed(86,29.376610, 47.988795,1597048503))
            add(LocationFeed(87,29.375243, 47.987125,1597048497))
            add(LocationFeed(88,29.377335, 47.989743,1597048503))
            add(LocationFeed(89,29.378467, 47.991241,1597048487))
            add(LocationFeed(90,29.376725, 47.992160,1597048486))
        }

        return mutableListOf<MutableList<LocationFeed>>().apply {

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
            add(feedsList18)
        }
    }

    fun insertLocation(location: Location) {
        val lastLocation = locationFeedsDao.loadLastLocation()
        if (lastLocation == null || (location.latitude != lastLocation.latitude && location.longitude != lastLocation.longitude)){
            val currentLocation = LocationFeed(latitude =  location.latitude, longitude = location.longitude, timestamp = System.currentTimeMillis()/1000)
            locationFeedsDao.insertLocation(currentLocation)
            //val lastLocationAdded = locationFeedsDao.loadLastLocation()
          // Log.d("Tracking-Logic", "last location added:  $lastLocationAdded")
        }
    }

    private fun distance(firstLocation: Location, lastLocation: Location) = firstLocation.distanceTo(lastLocation)

    private fun sendFeedsViaSocket(messageFeeds: String){
        val messageObject = JSONObject()
           val feedsArray = JSONArray(messageFeeds)
        try {
            messageObject.put("SendLocation", feedsArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        val message = "{\"SendLocation\":[{\"latitude\":29.378621666666664,\"longitude\":47.98415166666667,\"timestamp\":\"1602423598\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378621666666664,\"longitude\":47.98415166666667,\"timestamp\":\"1602423598\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"},{\"latitude\":29.378443333333333,\"longitude\":47.98430166666667,\"timestamp\":\"1602423600\"},{\"latitude\":29.37824333333333,\"longitude\":47.98437666666667,\"timestamp\":\"1602423601\"}]}"//messageObject.toString()
        val message = messageObject.toString()
       // Log.d("SignalR-Message",trackingMessage)
       // hubConnection.send("SendLocation", trackingMessage)
        Log.d("SignalR-SendLocation", "from dataLayer: $message")
        hubConnection.invoke("SendLocation", message)
       // Log.d("SignalR",message)
    }
}