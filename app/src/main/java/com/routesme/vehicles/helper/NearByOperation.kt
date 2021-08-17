package com.routesme.vehicles.helper

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.gson.Gson
import com.routesme.vehicles.data.model.NearbyData
import com.routesme.vehicles.data.model.PublishNearby
import org.greenrobot.eventbus.EventBus

class NearByOperation{
    lateinit var mContext: Context
    private var mMessageClient: MessagesClient? = null

    companion object{
        @get:Synchronized
        var instance = NearByOperation()


        var nearbyPublishOptions = PublishOptions.Builder()
                .setStrategy(nearbyPublishStrategy())
                .setCallback(object : PublishCallback() {
                    override fun onExpired() {
                        super.onExpired()
                        EventBus.getDefault().post(PublishNearby(true))
                        Log.d("NearbyMessagesApi","Publisher.. Publish onExpired")
                    }
                }).build()

        private fun nearbyPublishStrategy(): Strategy {
            return Strategy.Builder()
                    .setTtlSeconds(Strategy.TTL_SECONDS_MAX)
                    .setDistanceType(Strategy.DISTANCE_TYPE_DEFAULT)
                    .build()
        }
    }

    fun publish(message: String, context: Context) {
        mContext = context
        Log.d("NearbyMessagesApi","Publisher.. Context: $mContext")
        mMessageClient = Nearby.getMessagesClient(mContext!!)
        mMessageClient!!.publish(nearbyMessage(message), nearbyPublishOptions)
                .addOnSuccessListener {
                    Log.d("NearbyMessagesApi","Publisher.. Publish OnSuccess, message: $message")
                }.addOnFailureListener {
                    Log.d("NearbyMessagesApi","Publisher.. Publish OnFailure, message: $message")
                    EventBus.getDefault().post(PublishNearby(true))

                }.addOnCanceledListener {
                    Log.d("NearbyMessagesApi","Publisher.. Publish OnCanceled, message: $message")
                    EventBus.getDefault().post(PublishNearby(true))

                }.addOnCompleteListener {
                    Log.d("NearbyMessagesApi","Publisher.. Publish OnComplete, message: $message")
                }
    }

    fun unPublish(message: String,context: Context) {
        Nearby.getMessagesClient(context).unpublish(nearbyMessage(message))
    }

    private fun nearbyMessage(s: String) = Message(s.toByteArray())
    fun getNearbyDataJson(nearbyData: NearbyData): String =  Gson().toJson(nearbyData)
}