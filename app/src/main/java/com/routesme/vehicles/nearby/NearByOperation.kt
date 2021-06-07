package com.routesme.vehicles.nearby

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessagesClient
import com.google.gson.Gson
import com.routesme.vehicles.App
import com.routesme.vehicles.data.model.Parameter
import com.routesme.vehicles.view.events.PublishNearBy
import org.greenrobot.eventbus.EventBus

class NearByOperation {
    lateinit var mContext: Context
    private var current: Activity? = null
    private var mMessageClient: MessagesClient? = null
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



    fun publish(message: Parameter, context: Context) {
        mContext = context
        mMessageClient = Nearby.getMessagesClient(mContext!!)
        mMessageClient!!.publish(nearbyMessage(message), App.nearbyPublishOptions).addOnSuccessListener {

            Log.d("Publish","${message.DeviceId},${message.plateNo}")

        }.addOnFailureListener {

            Log.d("Publish","Failed")
            EventBus.getDefault().post(PublishNearBy(true))

        }.addOnCanceledListener {

            Log.d("Publish","Cancelled")
            EventBus.getDefault().post(PublishNearBy(true))

        }.addOnCompleteListener {

            Log.d("Publish","Complete")

        }
        /* Nearby.getMessagesClient(activity).publish(nearbyMessage(message), App.nearbyPublishOptions).addOnFailureListener {

             Log.d("Publish","Failure")

         }.addOnSuccessListener {

             Log.d("Publish","Success")

         }*/
        //messagesClient.publish(Message(message.), App.nearbyPublishOptions)


    }

    fun unPublish(message: Parameter, context: Context) {
        Nearby.getMessagesClient(context).unpublish(nearbyMessage(message))
    }

    private fun nearbyMessage(s: Parameter) = Message(Gson().toJson(s).toByteArray())
}