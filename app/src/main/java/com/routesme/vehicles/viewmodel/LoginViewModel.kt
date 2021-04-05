package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.SignInCredentials
import com.routesme.vehicles.data.repository.LoginRepository

class LoginViewModel : ViewModel() {

    fun signIn(signInCredentials: SignInCredentials, context: Context) = LoginRepository(context).signIn(signInCredentials)


}