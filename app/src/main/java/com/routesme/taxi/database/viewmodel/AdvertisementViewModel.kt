package com.routesme.taxi.database.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routesme.taxi.database.ResponseBody
import com.routesme.taxi.database.entity.AdvertisementTracking
import com.routesme.taxi.database.helper.DatabaseHelper
import com.routesme.taxi.utils.Period
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class RoomDBViewModel( private val dbHelper: DatabaseHelper) : ViewModel() {
    private val MIN = 100000000
    private val analyticsListLiveData = MutableLiveData<ResponseBody<List<AdvertisementTracking>>>()
    private val analyticsListAllLiveData = MutableLiveData<ResponseBody<List<AdvertisementTracking>>>()
    private val deleteTableLiveData = MutableLiveData<ResponseBody<Int>>()
    private val deleteAllTableLiveData = MutableLiveData<ResponseBody<Int>>()
    private val compositeDisposable = CompositeDisposable()
    fun insertLog(id:String, timeStamp:Long, period: Period, type:String) {
        viewModelScope.launch {
            try {
                var analysisRecord = dbHelper.getItem(id,timeStamp/MIN)
                Log.d("Thread ViewModel","${Thread.currentThread().name}, ${Thread.currentThread().id}")
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

    fun getAllList():LiveData<ResponseBody<List<AdvertisementTracking>>>{

        viewModelScope.launch {
            val getAllList =dbHelper.getAllList()
            analyticsListAllLiveData.postValue(ResponseBody.success(getAllList))
        }
        return analyticsListAllLiveData
    }

    fun deleteTable(currentDate: Long):LiveData<ResponseBody<Int>>{

        viewModelScope.launch {

            val deleteData = dbHelper.deleteTable(currentDate/MIN)
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

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()

        Log.d("ViewModel","onCleared")
    }

}