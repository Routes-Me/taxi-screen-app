package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.MVVM.Model.RegistrationCredentials
import com.routesme.taxi.MVVM.Repository.RegistrationRepository

class RegistrationViewModel() : ViewModel() {
    fun register(registrationCredentials: RegistrationCredentials, context: Context) = RegistrationRepository(context).register(registrationCredentials)
}