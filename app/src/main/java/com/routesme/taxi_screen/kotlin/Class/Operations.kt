package com.routesme.taxi_screen.kotlin.Class

import android.app.Activity
import android.content.Context
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.PublishOptions
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

    fun displayAlertDialog(context: Context, title: String, message: String){
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setIcon(android.R.drawable.ic_dialog_alert)
            setPositiveButton("OK"){ dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }

        val alertDialog: AlertDialog = builder.create().apply {
            setCancelable(false)
        }

        alertDialog.show()
    }

    fun publish(message: String, activity: Activity) {
        Nearby.getMessagesClient(activity).publish(nearbyMessage(message), App.nearbyPublishOptions)
    }

    fun unPublish(message: String, activity: Activity) {
        Nearby.getMessagesClient(activity).unpublish(nearbyMessage(message))
    }

    private fun nearbyMessage(s: String) = Message(s.toByteArray())
}