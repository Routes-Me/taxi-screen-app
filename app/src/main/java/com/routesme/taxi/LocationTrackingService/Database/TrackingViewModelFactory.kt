package com.routesme.taxi.LocationTrackingService.Database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrackingViewModelFactory(private val dbHelper: TrackingDatabaseHelper): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackingRoomDBViewModel::class.java)) {
            return TrackingRoomDBViewModel(dbHelper) as T
        }
       throw IllegalArgumentException("Unknown class name")
    }
}