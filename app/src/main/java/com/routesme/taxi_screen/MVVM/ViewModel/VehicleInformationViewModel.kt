package com.routesme.taxi_screen.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi_screen.MVVM.Repository.VehicleInformationRepository

class VehicleInformationViewModel() : ViewModel() {
    fun getInstitutions(offset: Int, limit: Int, context: Context) = VehicleInformationRepository(context).getInstitutions(offset,limit)
    fun getVehicles(institutionId: Int, offset: Int, limit: Int, context: Context) = VehicleInformationRepository(context).getVehicles(institutionId,offset,limit)
}