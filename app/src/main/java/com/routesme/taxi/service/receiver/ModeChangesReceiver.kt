package com.routesme.taxi.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.routesme.taxi.helper.DisplayManager

class ModeChangesReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        DisplayManager.instance.notifyRegisteredActivity()
    }
}