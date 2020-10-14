package com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment

import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.routesme.taxi_screen.Class.*
import com.routesme.taxi_screen.MVVM.Model.*
import com.routesme.taxi_screen.MVVM.ViewModel.ContentViewModel
import com.routesme.taxiscreen.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.content_fragment.view.*
import java.io.IOException

class ContentFragment : Fragment(),  ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var mContext: Context
    private var qRCodeCallback: QRCodeCallback? = null
    private lateinit var mView: View
    private var connectivityReceiver: ConnectivityReceiver? = null
    private var isDataFetched = false

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.content_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        qRCodeCallback?.let { it -> DisplayAdvertisements.instance.setQrCodeCallback(it) }
        checkConnection()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        DisplayAdvertisements.instance.release()
        connectivityReceiverRegistering(false)
        if (ConnectivityReceiver.connectivityReceiverListener != null)ConnectivityReceiver.connectivityReceiverListener = null
        super.onDestroy()
    }

    private fun checkConnection() {
        val isConnected = ConnectivityReceiver.isConnected
        if (isConnected) {
            fetchContent()
        } else {
            networkListener()
        }
    }

    private fun networkListener() {
        connectivityReceiver = ConnectivityReceiver()
        connectivityReceiverRegistering(true)
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected && !isDataFetched) {
            fetchContent()
            connectivityReceiverRegistering(false)
        }
    }

    private fun connectivityReceiverRegistering(register: Boolean) {
        try {
            if (register) {
                val intentFilter = IntentFilter("com.routesme.taxi_screen.SOME_ACTION").apply {
                    addAction("android.net.conn.CONNECTIVITY_CHANGE")
                }
                connectivityReceiver?.let { LocalBroadcastManager.getInstance(mContext).registerReceiver(it, intentFilter) }
            } else {
                connectivityReceiver?.let { LocalBroadcastManager.getInstance(mContext).unregisterReceiver(it) }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun fetchContent(){
        val dialog = SpotsDialog.Builder().setContext(mContext).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
        dialog?.show()
        val contentViewModel: ContentViewModel by viewModels()
        contentViewModel.getContent(1,100,mContext).observe(viewLifecycleOwner , Observer<ContentResponse> {
            dialog?.dismiss()
            if (it != null) {
                if (it.isSuccess) {
                    isDataFetched = true
                    val images = it.imageList.toList()
                    val videos = it.videoList.toList()
                    if (images.isNullOrEmpty() && videos.isNullOrEmpty()){
                        Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.no_data_found))
                        return@Observer
                    }else{
                        if (!images.isNullOrEmpty()) DisplayAdvertisements.instance.displayAdvertisementBannerList(images, mView.advertisementsImageView)
                        if (!videos.isNullOrEmpty()) {  DisplayAdvertisements.instance.displayAdvertisementVideoList(videos, mView.playerView, mView.videoRingProgressBar)}
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