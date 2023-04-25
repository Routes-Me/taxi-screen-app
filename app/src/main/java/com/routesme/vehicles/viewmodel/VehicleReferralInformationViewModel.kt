package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.repository.VehicleReferralInformationRepository

class VehicleReferralInformationViewModel : ViewModel() {
    fun getVehicleReferralInformation(vehicleId: String, context: Context) = VehicleReferralInformationRepository(context).getVehicleReferral(vehicleId)
}