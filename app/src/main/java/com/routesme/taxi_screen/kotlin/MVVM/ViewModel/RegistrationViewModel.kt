package com.routesme.taxi_screen.kotlin.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi_screen.kotlin.MVVM.Model.RegistrationCredentials
import com.routesme.taxi_screen.kotlin.MVVM.Repository.RegistrationRepository

class RegistrationViewModel() : ViewModel() {
    fun register(registrationCredentials: RegistrationCredentials, context: Context) = RegistrationRepository(context).register(registrationCredentials)
}