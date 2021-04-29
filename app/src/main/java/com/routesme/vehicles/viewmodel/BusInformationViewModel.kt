package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.repository.BusInformationRepository

class BusInformationViewModel : ViewModel() {
    fun getBusInformation(vehicleId: String, include: String, context: Context) = BusInformationRepository(context).getBusInformation(vehicleId, include)
}