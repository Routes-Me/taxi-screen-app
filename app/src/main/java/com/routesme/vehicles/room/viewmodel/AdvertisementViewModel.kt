package com.routesme.vehicles.room.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routesme.vehicles.helper.DateHelper
import com.routesme.vehicles.room.ResponseBody
import com.routesme.vehicles.room.entity.AdvertisementTracking
import com.routesme.vehicles.room.helper.DatabaseHelper
import com.routesme.vehicles.view.utils.Period
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomDBViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {
    private val MIN = 100000000
    
    private val analyticsListLiveData = MutableLiveData<ResponseBody<List<AdvertisementTracking>>>()
    private val analyticsListAllLiveData = MutableLiveData<ResponseBody<List<AdvertisementTracking>>>()
    private val deleteTableLiveData = MutableLiveData<ResponseBody<Int>>()
    private val deleteAllTableLiveData = MutableLiveData<ResponseBody<Int>>()

    fun insertLog(advertisementId: String, resourceName: String,timeStamp: Long, period: Period, type: String) {
        val currentDate = DateHelper.instance.getDateString(timeStamp)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var analysisRecord = dbHelper.getItem(resourceName, currentDate)
                if (analysisRecord != null) {
                    update(analysisRecord.id, period)
                } else {
                    dbHelper.insertAdvertisement(AdvertisementTracking(advertisementId = advertisementId, resourceName = resourceName, date = timeStamp, morning = 0, noon = 0, evening = 0, night = 0, time_in_day = currentDate, media_type = type))
                    var lastItem = dbHelper.getLastItem(resourceName, currentDate)
                    update(lastItem.id, period)
                }

            } catch (e: Exception) {

            }
        }
    }

    fun update(id: Int, period: Period) {
        viewModelScope.launch {
            try {
                when (period) {
                    Period.MORNING -> dbHelper.updateSlotMorning(id)
                    Period.NOON -> dbHelper.updateSlotNoon(id)
                    Period.EVENING -> dbHelper.updateSlotEvening(id)
                    Period.NIGHT -> dbHelper.updateSlotNight(id)
                }
            } catch (e: Exception) {

            }
        }
    }

    fun getAllList(): LiveData<ResponseBody<List<AdvertisementTracking>>> {

        viewModelScope.launch(Dispatchers.IO) {
            val getAllList = dbHelper.getAllList()
            analyticsListAllLiveData.postValue(ResponseBody.success(getAllList))
        }
        return analyticsListAllLiveData
    }


    fun deleteAllData(): LiveData<ResponseBody<Int>> {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteData = dbHelper.deleteAllTable()
            deleteAllTableLiveData.postValue(ResponseBody.success(deleteData))
        }
        return deleteAllTableLiveData
    }

}