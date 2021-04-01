package com.routesme.taxi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.data.model.RegistrationCredentials
import com.routesme.taxi.data.repository.RegistrationRepository

class RegistrationViewModel : ViewModel() {
    fun register(registrationCredentials: RegistrationCredentials, context: Context) = RegistrationRepository(context).register(registrationCredentials)
}