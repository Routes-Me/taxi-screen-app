package com.routesme.vehicles.room.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.routesme.vehicles.room.helper.DatabaseHelper
import com.routesme.vehicles.room.viewmodel.RoomDBViewModel

class ViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomDBViewModel::class.java)) {
            return RoomDBViewModel(dbHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}