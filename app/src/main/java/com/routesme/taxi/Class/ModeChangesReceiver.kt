package com.routesme.taxi.Class

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ModeChangesReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        DisplayManager.instance.notifyRegisteredActivity()
    }
}