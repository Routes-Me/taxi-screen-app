package com.routesme.taxi_screen.Class

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.routesme.taxi_screen.Class.App.Companion.instance
import java.util.*


class ConnectivityReceiver: BroadcastReceiver()  {

    private val connectivityStatus: Boolean
        get () {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnected
        }



    lateinit var context: Context
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        connectivityReceiverListener?.onNetworkConnectionChanged(connectivityStatus)

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
    }
}