package com.routesme.taxi_screen.kotlin.Class

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.routesme.taxi_screen.java.Detect_Network_Connection_Status.ConnectivityReceiver
import com.routesme.taxi_screen.java.Detect_Network_Connection_Status.ConnectivityReceiver.ConnectivityReceiverListener

class App : Application() {
    private var proxy: HttpProxyCacheServer? = null
    var technicalSupportUserName: String? = null
    var technicalSupportPassword: String? = null
    var isNewLogin = false
    var taxiOfficeId = 0
    var taxiPlateNumber: String? = null
    var taxiOfficeName: String? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun setConnectivityListener(listener: ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(this).maxCacheSize(java.lang.Long.valueOf(1024 * 1024 * 1024.toLong()) * 30).build()
    }

    companion object {
        @get:Synchronized
        var instance: App? = null
            private set

        fun getProxy(context: Context): HttpProxyCacheServer {
            val app = context.applicationContext as App
            return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy!!
        }
    }
}