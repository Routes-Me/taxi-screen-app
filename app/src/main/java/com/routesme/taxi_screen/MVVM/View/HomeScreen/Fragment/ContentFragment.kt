package com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi_screen.Class.*
import com.routesme.taxi_screen.Class.VideoPreLoading.Constants
import com.routesme.taxi_screen.Class.VideoPreLoading.VideoPreLoadingService
import com.routesme.taxi_screen.MVVM.Model.*
import com.routesme.taxi_screen.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi_screen.helper.SharedPreferencesHelper
import com.routesme.taxiscreen.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.content_fragment.view.*
import java.io.IOException

class ContentFragment : Fragment(),  ConnectivityReceiver.ConnectivityReceiverListener {

    private val operations = Operations.instance
    private lateinit var tabletSerialNumber:String
    private lateinit var mContext: Context
    private var qRCodeCallback: QRCodeCallback? = null
    private lateinit var view1: View
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val connectivityReceiver = ConnectivityReceiver()
    private lateinit var intentFilter: IntentFilter
    private var isConnected = false
    private var isDataFetched = false
    private lateinit var displayAdvertisements: DisplayAdvertisements
    private var dialog: AlertDialog? = null
    private var isNetworkAvailable = false

    companion object {
        val instance = ContentFragment()
    }

    override fun onAttach(context: Context) {
        mContext = context
        sharedPreferences = context.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        // tabletSerialNumber = this.sharedPreferences.getString("tabletSerialNo", null)
        // firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        // firebaseAnalytics.setUserId(tabletSerialNumber)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view1 = inflater.inflate(R.layout.content_fragment, container, false)

        initAdvertiseViews()
        displayAdvertisements = DisplayAdvertisements.instance
        qRCodeCallback?.let { it1 -> displayAdvertisements.setQrCodeCallback(it1) }
       // checkConnection()
        return view1
    }

    override fun onResume() {

        super.onResume()
    }

    override fun onPause() {

        super.onPause()
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onDestroyView() {
        displayAdvertisements.release()
        connectivityReceiverRegistering(false)
        super.onDestroyView()
    }

    private fun initAdvertiseViews() {
        dialog = SpotsDialog.Builder().setContext(mContext).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
    }

    private fun updateFirebaseAnalystics(itemAnalytics: ItemAnalytics) {
        val params = Bundle()
        params.putString("device_id", tabletSerialNumber)
        params.putInt("id", itemAnalytics.id)
        params.putString("name", itemAnalytics.name)
        firebaseAnalytics.logEvent(itemAnalytics.name, params)
    }

    private fun checkConnection() {
        isConnected = ConnectivityReceiver.isConnected
        if (isConnected) {
           // fetchContent()
        } else {
            networkListener()
        }
    }

    private fun networkListener() {
        connectivityReceiverRegistering(true)
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected && !isDataFetched) {
           // fetchContent()
            connectivityReceiverRegistering(false)
        }
    }

    private fun connectivityReceiverRegistering(register: Boolean) {
        try {
            if (register) {
                intentFilter = IntentFilter("com.routesme.taxi_screen.SOME_ACTION")
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
                // mContext.registerReceiver(connectivityReceiver, intentFilter)
                LocalBroadcastManager.getInstance(mContext).registerReceiver(connectivityReceiver, intentFilter)
            } else {
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(connectivityReceiver)
                // mContext.unregisterReceiver(connectivityReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }


    private fun fetchContent(){
        dialog?.show()
        val contentViewModel: ContentViewModel by viewModels()
        contentViewModel.getContent(1,100,mContext).observe(viewLifecycleOwner , Observer<ContentResponse> {
            dialog?.dismiss()
            if (it != null) {
                if (it.isSuccess) {
                    val images = it.imageList.toList()
                    val videos = it.videoList.toList()
                    isDataFetched = true
                    if (images.isNullOrEmpty() && videos.isNullOrEmpty()){
                        operations.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.no_data_found))
                        return@Observer
                    }else{
                        if (!images.isNullOrEmpty()) displayAdvertisements.displayAdvertisementBannerList(images,view1.advertisementsImageView)
                        if (!videos.isNullOrEmpty()) {  displayAdvertisements.displayAdvertisementVideoList(videos,view1.playerView,view1.videoRingProgressBar)}
                    }

                } else {
                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let { errors -> displayErrors(errors) }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            operations.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.network_Issue))
                        } else {
                            operations.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.conversion_Issue))
                        }
                    }
                }
            } else {
                operations.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.unknown_error))
            }
        })
    }

    private fun displayErrors(errors: List<Error>) {
        for (error in errors) {
            operations.displayAlertDialog(mContext, getString(R.string.content_error_title), "Error message: ${error.detail}")
        }
    }

    private fun startPreLoadingService(it: List<Data>) {
        val videoList = ArrayList<String>()
        for (video in it){
            videoList.add(video.url.toString())
        }
        val preLoadingServiceIntent = Intent(mContext, VideoPreLoadingService::class.java)
        preLoadingServiceIntent.putStringArrayListExtra(Constants.VIDEO_LIST, videoList)
        context?.startService(preLoadingServiceIntent)
    }
}