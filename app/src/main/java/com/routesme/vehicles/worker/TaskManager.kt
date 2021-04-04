package com.routesme.vehicles.worker

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.routesme.vehicles.api.RestApiService
import com.routesme.vehicles.helper.DateHelper
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.room.AdvertisementDatabase
import com.routesme.vehicles.room.entity.AdvertisementTracking
import com.routesme.vehicles.room.helper.DatabaseHelperImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams),CoroutineScope by MainScope() {
    private var dbHelper = DatabaseHelperImpl(AdvertisementDatabase.invoke(context))
    private val MIN = 100000000
    private var sharedPreferences = context.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
    private var editior = sharedPreferences?.edit()
    val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    override fun doWork(): Result {
        try {
            val device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!
            launch {
                val list = dbHelper.getList(DateHelper.instance.getCurrentDate() / MIN)
                device_id.let { deviceId ->
                    if (!list.isNullOrEmpty()) {
                        val call = thisApiCorService.postReport(getJsonArray(list), device_id)
                        call.enqueue(object : Callback<JsonElement> {
                            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                                if (response.isSuccessful) {
                                    launch(Dispatchers.IO) {
                                        val delete = dbHelper.deleteTable(DateHelper.instance.getCurrentDate() / MIN)
                                        Log.d("Workmanager","${delete}")
                                        editior?.putString(SharedPreferencesHelper.from_date, DateHelper.instance.getCurrentDate().toString())
                                        editior?.commit()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {


                            }
                        })

                    } else {

                        Log.d("WorkManager", "No Data found")
                    }
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }

    }

    private fun getJsonArray(list: List<AdvertisementTracking>): JsonArray {
        val jsonArray = JsonArray()
        list.forEach {
            val jsonObject = JsonObject().apply {
                addProperty("date", it.date / 1000)
                addProperty("advertisementId", it.advertisementId)
                addProperty("mediaType", it.media_type)
                add("slots", getJsonArrayOfSlot(it.morning, it.noon, it.evening, it.night))
            }
            jsonArray.add(jsonObject)
        }

        return jsonArray

    }

    private fun getJsonArrayOfSlot(morning: Int, noon: Int, evening: Int, night: Int): JsonArray {
        val jsonArray = JsonArray()
        if (morning != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "mo")
            jsonObject.addProperty("value", morning)
            jsonArray.add(jsonObject)
        }
        if (noon != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "no")
            jsonObject.addProperty("value", noon)
            jsonArray.add(jsonObject)
        }
        if (evening != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "ev")
            jsonObject.addProperty("value", evening)
            jsonArray.add(jsonObject)
        }
        if (night != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("period", "ni")
            jsonObject.addProperty("value", night)
            jsonArray.add(jsonObject)
        }

        return jsonArray

    }

}
