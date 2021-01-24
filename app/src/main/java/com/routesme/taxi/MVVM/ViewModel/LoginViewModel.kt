package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.MVVM.Model.SignInCredentials
import com.routesme.taxi.MVVM.Repository.LoginRepository
import com.routesme.taxi.MVVM.Repository.UnlinkRepository

class LoginViewModel : ViewModel() {

    fun signIn(signInCredentials: SignInCredentials, context: Context) = LoginRepository(context).signIn(signInCredentials)


}