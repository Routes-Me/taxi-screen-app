package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.BusPaymentProcessCredentials
import com.routesme.vehicles.data.repository.BusPaymentProcessRepository

class BusPaymentProcessViewModel : ViewModel()  {
    fun processed(busPaymentProcessCredentials: BusPaymentProcessCredentials, context: Context) = BusPaymentProcessRepository(context).processed(busPaymentProcessCredentials)
}