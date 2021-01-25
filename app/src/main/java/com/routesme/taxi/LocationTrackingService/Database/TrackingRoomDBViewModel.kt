package com.routesme.taxi.LocationTrackingService.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routesme.taxi.LocationTrackingService.Model.LocationFeed
import kotlinx.coroutines.launch

class TrackingRoomDBViewModel(private val dbHelper: TrackingDatabaseHelper): ViewModel() {

    private val locationFeeds = MutableLiveData<List<LocationFeed>>()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            locationFeeds.postValue(null)
            try {
                val usersFromDb = dbHelper.getFeeds()
                    locationFeeds.postValue(usersFromDb)
            } catch (e: Exception) {
                locationFeeds.postValue(null)
            }
        }
    }

    fun getFeeds(): LiveData<List<LocationFeed>> {
        return locationFeeds
    }
}