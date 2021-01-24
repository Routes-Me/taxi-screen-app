package com.routesme.taxi.database.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.database.ResponseBody
import com.routesme.taxi.database.entity.AdvertisementTracking
import com.routesme.taxi.database.helper.DatabaseHelper
import com.routesme.taxi.utils.Period
import kotlinx.coroutines.launch

public class RoomDBViewModel( private val dbHelper: DatabaseHelper) : ViewModel() {
    private val MIN = 100000000
    private val analyticsListLiveData = MutableLiveData<ResponseBody<List<AdvertisementTracking>>>()
    private val deleteTableLiveData = MutableLiveData<ResponseBody<Int>>()
    private val deleteAllTableLiveData = MutableLiveData<ResponseBody<Int>>()
    fun insertLog(id:String, timeStamp:Long, period: Period, type:String) {
        viewModelScope.launch {
            try {
                var analysisRecord = dbHelper.getItem(id,timeStamp/MIN)

                if(analysisRecord!=null){

                    update(analysisRecord.id,period)

                }else{

                    dbHelper.insertAdvertisement(AdvertisementTracking(advertisementId = id, date = timeStamp, morning = 0, noon = 0, evening = 0, night = 0, time_in_day = timeStamp / MIN, media_type = type))
                    var lastItem = dbHelper.getLastItem(id,timeStamp/MIN)
                    update(lastItem.id,period)

                }

            } catch (e: Exception) {


            }
        }
    }

    fun update(id: Int,period:Period){

        viewModelScope.launch {
            try {

                when(period){

                    Period.MORNING -> dbHelper.updateSlotMorning(id)
                    Period.NOON -> dbHelper.updateSlotNoon(id)
                    Period.EVENING -> dbHelper.updateSlotEvening(id)
                    Period.NIGHT -> dbHelper.updateSlotNight(id)

                }
            }catch (e: Exception){


            }
        }

    }
    fun getReport(currentDate:Long): LiveData<ResponseBody<List<AdvertisementTracking>>> {

        viewModelScope.launch {

            val getReport =dbHelper.getList(currentDate/MIN)
            analyticsListLiveData.postValue(ResponseBody.success(getReport))

        }
        return analyticsListLiveData
    }

    fun deleteTable(currentDate: Long):LiveData<ResponseBody<Int>>{

        viewModelScope.launch {

            val deleteData = dbHelper.deleteTable(currentDate)
            deleteTableLiveData.postValue(ResponseBody.success(deleteData))
        }
        return deleteTableLiveData

    }

    fun deleteAllData():LiveData<ResponseBody<Int>>{

        viewModelScope.launch {

            val deleteData = dbHelper.deleteAllTable()
            deleteAllTableLiveData.postValue(ResponseBody.success(deleteData))
        }
        return deleteAllTableLiveData

    }





}