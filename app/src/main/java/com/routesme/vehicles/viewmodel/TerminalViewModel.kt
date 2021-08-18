package com.routesme.vehicles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.routesme.vehicles.data.model.TerminalCredentials
import com.routesme.vehicles.data.repository.TerminalRepository

class TerminalViewModel : ViewModel() {
    fun register(terminalCredentials: TerminalCredentials, context: Context) = TerminalRepository(context).register(terminalCredentials)
    fun update(terminalId: String, terminalCredentials: TerminalCredentials, context: Context) = TerminalRepository(context).update(terminalId, terminalCredentials)
}