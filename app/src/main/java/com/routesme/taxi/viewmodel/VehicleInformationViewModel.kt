package com.routesme.taxi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.data.repository.VehicleInformationRepository

class VehicleInformationViewModel : ViewModel() {
    fun getInstitutions(offset: Int, limit: Int, context: Context) = VehicleInformationRepository(context).getInstitutions(offset, limit)
    fun getVehicles(institutionId: String, offset: Int, limit: Int, context: Context) = VehicleInformationRepository(context).getVehicles(institutionId, offset, limit)
}