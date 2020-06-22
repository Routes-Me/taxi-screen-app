package com.routesme.taxi_screen.kotlin.Class

import android.content.Context
import android.widget.Button
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.routesme.taxiscreen.R

class Operations {
    lateinit var context: Context

    companion object{
        @get:Synchronized
        var instance = Operations()
    }

    //Enable / Disable Next Button ...
    fun enableNextButton(button: Button, enable: Boolean) {
        if (enable) {
            button.setBackgroundResource(R.drawable.next_button_border_enable)
            button.isEnabled = true
        } else {
            button.setBackgroundResource(R.drawable.next_button_border_disable)
            button.isEnabled = false
        }
    }

    fun publish(message: String) {
        Nearby.getMessagesClient(context).publish(nearbyMessage(message))
    }

    fun unPublish(message: String) {
        Nearby.getMessagesClient(context).unpublish(nearbyMessage(message))
    }

    private fun nearbyMessage(s: String) = Message(s.toByteArray())
}