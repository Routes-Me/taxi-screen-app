package com.routesme.taxi.MVVM.View.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.routesme.taxi.Class.AdvertisementsHelper
import com.routesme.taxi.Class.ConnectivityReceiver
import com.routesme.taxi.Class.Operations
import com.routesme.taxi.MVVM.Model.ContentResponse
import com.routesme.taxi.MVVM.Model.Error
import com.routesme.taxi.MVVM.Model.QRCodeCallback
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.content_fragment.view.*
import java.io.IOException


class ContentFragment : Fragment(),SimpleExoPlayer.VideoListener {

    private lateinit var mContext: Context
    private var qRCodeCallback: QRCodeCallback? = null
    private lateinit var mView: View
    private var connectivityReceiver: ConnectivityReceiver? = null
    private var isDataFetched = false
    private var dialog: SpotsDialog? = null
    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0
    val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

    override fun onAttach(context: Context) {
        mContext = context
        try {
            qRCodeCallback = activity as QRCodeCallback
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement QRCodeCallback")
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        qRCodeCallback = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view : View = inflater.inflate(R.layout.content_fragment, container, false)
        requireActivity().registerReceiver(myReceiver, intentFilter);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        qRCodeCallback?.let { it -> AdvertisementsHelper.instance.setQrCodeCallback(it) }
        super.onViewCreated(view, savedInstanceState)
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = getConnectivityStatusString(context)
            Log.d("Status",status)
            if (status != "No" && !isDataFetched) {
                fetchContent()
            }
        }
    }

    fun getConnectivityStatusString(context: Context): String? {
        val conn = getConnectivityStatus(context)
        var status: String? = null
        if (conn == TYPE_WIFI) {
            status = "Wifi" //"Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile" //"Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "No" //"Not connected to Internet";
        }
        return status
    }

    fun getConnectivityStatus(context: Context): Int {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    override fun onDestroy() {
        AdvertisementsHelper.instance.release()
        super.onDestroy()
    }

    /*private fun checkConnection() {
        val isConnected = ConnectivityReceiver.isConnected
        if (isConnected) {
            fetchContent()
        }/* else {
            networkListener()
        }*/
    }*/

    /*private fun networkListener() {
        connectivityReceiver = ConnectivityReceiver()
        //connectivityReceiverRegistering(true)
        ConnectivityReceiver.connectivityReceiverListener = this
    }*/

    private fun connectivityReceiverRegistering(register: Boolean) {
        try {
            if (register) {
                /*val intentFilter = IntentFilter("com.routesme.taxi_screen.SOME_ACTION").apply {
                    addAction("android.net.conn.CONNECTIVITY_CHANGE")
                }*/
                //connectivityReceiver?.let { activity.getInstance(mContext).registerReceiver(it, intentFilter) }
                connectivityReceiver?.let { requireActivity().registerReceiver(it, intentFilter) }
            } else {
                connectivityReceiver?.let { requireActivity().unregisterReceiver(it) }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
    private fun fetchContent(){
        val contentViewModel: ContentViewModel by viewModels()
        contentViewModel.getContent(1,100,mContext).observe(viewLifecycleOwner , Observer<ContentResponse> {
            dialog?.dismiss()
            Log.d("fetchContent-dialog","dialog")
            if (it != null) {
                if (it.isSuccess) {
                    isDataFetched = true
                    val images = it.imageList.toList()
                    val videos = it.videoList.toList()
                    if (images.isNullOrEmpty() && videos.isNullOrEmpty()){
                        Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.no_data_found))
                        return@Observer
                    }else{
                        if (!images.isNullOrEmpty()) AdvertisementsHelper.instance.displayImages(images, mView.advertisementsImageView)
                          AdvertisementsHelper.instance.displayVideos(mContext, videos, mView.playerView, mView.videoRingProgressBar)
                    }

                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.network_Issue))
                        } else {
                            Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun displayErrors(errors: List<Error>) {
        for (error in errors) {
            Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), "Error message: ${error.detail}")
        }
    }


}
