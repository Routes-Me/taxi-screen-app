package com.routesme.taxi_screen.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi_screen.MVVM.Model.SignInCredentials
import com.routesme.taxi_screen.MVVM.Repository.LoginRepository

class LoginViewModel() : ViewModel() {
    fun signIn(signInCredentials: SignInCredentials, context: Context) = LoginRepository(context).signIn(signInCredentials)
}