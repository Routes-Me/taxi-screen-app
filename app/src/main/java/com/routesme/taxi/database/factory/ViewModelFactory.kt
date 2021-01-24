package com.routesme.taxi.database.factory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.routesme.taxi.MVVM.View.fragment.ContentFragment
import com.routesme.taxi.database.helper.DatabaseHelper
import com.routesme.taxi.database.viewmodel.RoomDBViewModel

class ViewModelFactory(private val dbHelper: DatabaseHelper) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomDBViewModel::class.java)) {
            return RoomDBViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}