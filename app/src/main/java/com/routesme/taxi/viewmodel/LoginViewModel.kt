package com.routesme.taxi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.data.model.SignInCredentials
import com.routesme.taxi.data.repository.LoginRepository

class LoginViewModel : ViewModel() {

    fun signIn(signInCredentials: SignInCredentials, context: Context) = LoginRepository(context).signIn(signInCredentials)


}