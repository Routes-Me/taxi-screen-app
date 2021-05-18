package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.repository.CarrierInformationRepository

class CarrierInformationViewModel : ViewModel() {
    fun getCarrierInformation(vehicleId: String, include: String, context: Context) = CarrierInformationRepository(context).getCarrierInformation(vehicleId, include)
}