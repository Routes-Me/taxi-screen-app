package com.routesme.vehicles.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.routesme.vehicles.helper.DisplayManager

class ModeChangesReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        DisplayManager.instance.notifyRegisteredActivity()
    }
}