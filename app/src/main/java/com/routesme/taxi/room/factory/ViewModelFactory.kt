package com.routesme.taxi.room.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.routesme.taxi.room.helper.DatabaseHelper
import com.routesme.taxi.room.viewmodel.RoomDBViewModel

class ViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomDBViewModel::class.java)) {
            return RoomDBViewModel(dbHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}