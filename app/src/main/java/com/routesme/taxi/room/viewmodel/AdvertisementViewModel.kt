package com.routesme.taxi.room.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routesme.taxi.room.ResponseBody
import com.routesme.taxi.room.entity.AdvertisementTracking
import com.routesme.taxi.room.helper.DatabaseHelper
import com.routesme.taxi.view.utils.Period
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomDBViewModel( private val dbHelper: DatabaseHelper) : ViewModel() {

    companion object{

        const val KEY = "RoomDBViewModel"
    }

    private val MIN = 100000000
    private val analyticsListLiveData = MutableLiveData<ResponseBody<List<AdvertisementTracking>>>()
    private var advertisementList = ArrayList<AdvertisementTracking>()
    private val analyticsListAllLiveData = MutableLiveData<ResponseBody<List<AdvertisementTracking>>>()
    private val deleteTableLiveData = MutableLiveData<ResponseBody<Int>>()
    private val deleteAllTableLiveData = MutableLiveData<ResponseBody<Int>>()
    fun insertLog(id:String, timeStamp:Long, period: Period, type:String) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            val getReport =dbHelper.getList(currentDate/MIN)
            Log.d("WorkerManager","DATA OUTSIDE VIEWMODEL ${getReport}")
            if(!getReport.isNullOrEmpty()){
                analyticsListLiveData.postValue(ResponseBody.success(getReport))
                //analyticsListLiveData.value = null
                Log.d("WorkerManager","DATA INSIDE VIEWMODEL is NOT NULL ${getReport}")
            }else{
                Log.d("WorkerManager","DATA INSIDE VIEWMODEL ${getReport}")
                analyticsListLiveData.postValue(ResponseBody.error("No Data Found",getReport))
            }

        }
        return analyticsListLiveData
    }

   /* fun getReport(currentDate:Long): List<AdvertisementTracking> {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("WorkerManager","DATA INSIDE VIEWMODEL is NOT NULL ${dbHelper.getList(currentDate / MIN)}")
            val list = dbHelper.getList(currentDate / MIN)
           list.forEach {

               advertisementList.a

           }
        }
        return list
    }*/
    fun getAllList():LiveData<ResponseBody<List<AdvertisementTracking>>>{

        viewModelScope.launch(Dispatchers.IO) {
            val getAllList =dbHelper.getAllList()
            analyticsListAllLiveData.postValue(ResponseBody.success(getAllList))
        }
        return analyticsListAllLiveData
    }

    fun deleteTable(currentDate: Long):LiveData<ResponseBody<Int>>{
        viewModelScope.launch(Dispatchers.IO) {
            val deleteData = dbHelper.deleteTable(currentDate/MIN)
            deleteTableLiveData.postValue(ResponseBody.success(deleteData))

        }
        return deleteTableLiveData
    }

    fun deleteAllData():LiveData<ResponseBody<Int>>{
        viewModelScope.launch(Dispatchers.IO) {
            val deleteData = dbHelper.deleteAllTable()
            deleteAllTableLiveData.postValue(ResponseBody.success(deleteData))
        }
        return deleteAllTableLiveData
    }

    override fun onCleared() {

        super.onCleared()
    }

}