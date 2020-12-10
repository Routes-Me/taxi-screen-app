package com.routesme.taxi.MVVM.NearBy

import android.app.Activity
import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.routesme.taxi.uplevels.App

class NearByOperation {
    lateinit var context: Context

    companion object{
        @get:Synchronized
        var instance = NearByOperation()
    }

    //Enable / Disable Next Button ...
    /*fun enableNextButton(button: Button, enable: Boolean) {
        if (enable) {
            button.setBackgroundResource(R.drawable.next_button_border_enable)
            button.isEnabled = true
        } else {
            button.setBackgroundResource(R.drawable.next_button_border_disable)
            button.isEnabled = false
        }
    }*/

    fun publish(message: String, activity: Activity) {
        Nearby.getMessagesClient(activity).publish(nearbyMessage(message), App.nearbyPublishOptions)
    }

    fun unPublish(message: String, activity: Activity) {
        Nearby.getMessagesClient(activity).unpublish(nearbyMessage(message))
    }

    private fun nearbyMessage(s: String) = Message(s.toByteArray())
}