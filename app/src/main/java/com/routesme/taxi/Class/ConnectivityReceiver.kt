package com.routesme.taxi.Class

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*


class ConnectivityReceiver: BroadcastReceiver()  {

    class NetworkObservable private constructor() : Observable() {
        fun connectionChanged() {
            setChanged()
            notifyObservers()
        }

        companion object {
            var instance: NetworkObservable? = null
                get() {
                    if (field == null) {
                        field = NetworkObservable()
                    }
                    return field
                }
                private set
        }
    }

    fun getObservable(): NetworkObservable? {
        return NetworkObservable.instance
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("NetworkChangeReceiver","Connection status changed");
        //connectivityReceiverListener?.onNetworkConnectionChanged(getObservable()!!.connectionChanged();)
        getObservable()!!.connectionChanged();
    }
    /*private val connectivityStatus: Boolean
        get () {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnected
        }

    lateinit var context: Context
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        connectivityReceiverListener?.onNetworkConnectionChanged(connectivityStatus)
        Log.d("Checking Network", connectivityStatus.toString())

    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
        val isConnected: Boolean
            get() {
                val cm = Objects.requireNonNull(instance)!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnected       }
    }*/
}