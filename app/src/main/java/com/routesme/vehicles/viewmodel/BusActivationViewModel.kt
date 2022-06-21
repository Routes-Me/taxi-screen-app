package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.BusActivationCredentials
import com.routesme.vehicles.data.repository.BusActivationRepository

class BusActivationViewModel : ViewModel() {
    fun activate(busActivationCredentials: BusActivationCredentials, context: Context) = BusActivationRepository(context).activate(busActivationCredentials)
    fun deactivate(busActivationCredentials: BusActivationCredentials, context: Context) = BusActivationRepository(context).deactivate(busActivationCredentials)
}