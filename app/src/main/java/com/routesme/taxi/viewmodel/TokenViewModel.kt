package com.routesme.taxi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.taxi.data.repository.TokenRepository

class TokenViewModel : ViewModel() {

    fun refreshToken(context: Context) = TokenRepository(context).refreshToken()
}