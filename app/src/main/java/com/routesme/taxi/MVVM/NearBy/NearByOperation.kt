package com.routesme.taxi.MVVM.NearBy

import android.R.id
import android.app.Activity
import android.content.Context
import android.content.ServiceConnection
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.gson.Gson
import com.routesme.taxi.MVVM.Model.Parameter
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.MVVM.events.PublishNearBy
import com.routesme.taxi.uplevels.App
import org.greenrobot.eventbus.EventBus


class NearByOperation {
    lateinit var mContext: Context
    private var current: Activity? = null
    private var mMessageClient: MessagesClient? = null

    companion object{
        @get:Synchronized
        var instance = NearByOperation()
        /*var nearbyPublishOptions = PublishOptions.Builder()
                .setStrategy(nearbyStrategy())
                .setCallback(object : PublishCallback() {
                    override fun onExpired() {
                        super.onExpired()
                        Log.d("Publish","Expire")
                        EventBus.getDefault().post(PublishNearBy(true))
                    }
                }).build()

        private fun nearbyStrategy(): Strategy {
            return Strategy.Builder()
                    .setTtlSeconds(Strategy.TTL_SECONDS_DEFAULT)
                    .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
                    .setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST)
                    .build()
        }*/
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



    fun publish(message: Parameter,context: Context) {
        mContext = context
        mMessageClient = Nearby.getMessagesClient(mContext!!)
        mMessageClient!!.publish(nearbyMessage(message),App.nearbyPublishOptions).addOnSuccessListener {

            Log.d("Publish",message.deviceID)

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

    fun unPublish(message: Parameter,context: Context) {
        Nearby.getMessagesClient(context).unpublish(nearbyMessage(message))
    }

    private fun nearbyMessage(s: Parameter) = Message(Gson().toJson(s).toByteArray())

}