package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.RegistrationCredentials
import com.routesme.vehicles.data.repository.RegistrationRepository

class RegistrationViewModel : ViewModel() {
    fun register(registrationCredentials: RegistrationCredentials, context: Context) = RegistrationRepository(context).register(registrationCredentials)
}