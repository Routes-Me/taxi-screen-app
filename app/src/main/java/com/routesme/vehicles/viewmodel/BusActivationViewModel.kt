package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.ActivateBusCredentials
import com.routesme.vehicles.data.repository.BusActivationRepository

class BusActivationViewModel : ViewModel() {
    fun activate(activateBusCredentials: ActivateBusCredentials, context: Context) = BusActivationRepository(context).activate(activateBusCredentials)
}