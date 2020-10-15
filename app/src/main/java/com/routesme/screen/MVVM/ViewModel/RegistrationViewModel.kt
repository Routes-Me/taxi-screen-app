package com.routesme.screen.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.screen.MVVM.Model.RegistrationCredentials
import com.routesme.screen.MVVM.Repository.RegistrationRepository

class RegistrationViewModel() : ViewModel() {
    fun register(registrationCredentials: RegistrationCredentials, context: Context) = RegistrationRepository(context).register(registrationCredentials)
}