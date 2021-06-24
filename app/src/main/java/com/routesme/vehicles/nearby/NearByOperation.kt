package com.routesme.vehicles.nearby

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
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

    fun publish(message: Parameter, context: Context) {
        mContext = context
        mMessageClient = Nearby.getMessagesClient(mContext!!)
        mMessageClient!!.publish(nearbyMessage(message), App.nearbyPublishOptions).addOnSuccessListener {
            Log.d("Publish","${message.DeviceId},${message.plateNo}")
            Toast.makeText(App.instance,"Publish ${message.DeviceId},${message.plateNo}", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {

            Log.d("Publish","Failed")
            Toast.makeText(App.instance,"Fail", Toast.LENGTH_LONG).show()
            EventBus.getDefault().post(PublishNearBy(true))

        }.addOnCanceledListener {

            Log.d("Publish","Cancelled")
            Toast.makeText(App.instance,"Cancelled", Toast.LENGTH_LONG).show()
            EventBus.getDefault().post(PublishNearBy(true))

        }.addOnCompleteListener {
            //Toast.makeText(App.instance,"Complete", Toast.LENGTH_LONG).show()
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