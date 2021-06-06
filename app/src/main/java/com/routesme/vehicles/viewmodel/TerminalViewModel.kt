package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.Parameter
import com.routesme.vehicles.data.repository.CarrierInformationRepository
import com.routesme.vehicles.data.repository.TerminalRepository

class TerminalViewModel : ViewModel() {
    fun createTerminal(parameter: Parameter,context: Context) = TerminalRepository(context).createTerminal(parameter)
}