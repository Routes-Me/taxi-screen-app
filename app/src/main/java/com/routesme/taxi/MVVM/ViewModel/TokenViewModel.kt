package com.routesme.taxi.MVVM.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.MVVM.Model.SignInCredentials
import com.routesme.taxi.MVVM.Repository.LoginRepository
import com.routesme.taxi.MVVM.Repository.TokenRepository

class TokenViewModel():ViewModel(){

    fun refreshToken(context: Context,refresh_token:String?) = TokenRepository(context,refresh_token).refreshToken(refresh_token)
}