package com.routesme.taxi.LocationTrackingService.Database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import kotlinx.coroutines.launch

class TrackingRoomDBViewModel(private val dbHelper: TrackingDatabaseHelper): ViewModel() {

    private val locationFeeds = MutableLiveData<List<LocationFeed>>()

    fun insertLocation(locationFeed: LocationFeed) {
        viewModelScope.launch {
            try {
                dbHelper.insertLocation(locationFeed)
            } catch (e: Exception) {
                Log.d("TrackingDatabaseOperations", "Insert Exception: ${e.message}")
            }
        }
    }

    fun getLocationFeeds(): LiveData<List<LocationFeed>> {
        viewModelScope.launch {
            val feeds = dbHelper.getFeeds()
            locationFeeds.postValue(feeds)
        }
        return locationFeeds
    }

    fun deleteFeed(id1: Int, id2: Int) {
        viewModelScope.launch {
            try {
                dbHelper.deleteFeeds(id1, id2)
            } catch (e: Exception) {
                Log.d("TrackingDatabaseOperations", "Insert Exception: ${e.message}")
            }
        }
    }
}