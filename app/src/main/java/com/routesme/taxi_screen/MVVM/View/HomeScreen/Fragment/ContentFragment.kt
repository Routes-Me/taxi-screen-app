package com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi_screen.Class.*
import com.routesme.taxi_screen.MVVM.Model.*
import com.routesme.taxi_screen.MVVM.Model.ContentResponse
import com.routesme.taxi_screen.MVVM.View.HomeScreen.Activity.HomeActivity
import com.routesme.taxi_screen.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi_screen.Class.VideoPreLoading.Constants
import com.routesme.taxi_screen.Class.VideoPreLoading.VideoPreLoadingService
import com.routesme.taxiscreen.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.content_fragment.view.*
import java.io.IOException

class ContentFragment : Fragment(), View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

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

    companion object {
        val instance = ContentFragment()
    }

    override fun onAttach(context: Context) {
        mContext = context
        sharedPreferences = context.getSharedPreferences(SharedPreference.device_data, Activity.MODE_PRIVATE)
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
        checkConnection()

        return view1
    }

    private fun initAdvertiseViews() {
        dialog = SpotsDialog.Builder().setContext(mContext).setTheme(R.style.SpotsDialogStyle).setCancelable(false).build()
        view1.apply {
            Advertisement_Banner_CardView.setOnClickListener(instance)
            Advertisement_Video_CardView.setOnClickListener(instance)
        }
        // val videoRingProgressBar = contentFragment.contentFragmentView.videoRingProgressBar
        // val advertisementsVideoView = contentFragment.contentFragmentView.advertisementsVideoView
        // val advertisementsImageView = contentFragment.contentFragmentView.advertisementsImageView

    }

    override fun onDestroy() {

        connectivityReceiverRegistering(false)
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // R.id.Advertisement_Banner_CardView -> updateFirebaseAnalystics(ItemAnalytics(2, "click_banner"))
            // R.id.Advertisement_Video_CardView -> updateFirebaseAnalystics(ItemAnalytics(1, "click_video"))
        }
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
            fetchContent()
        } else {
            networkListener()
        }
    }

    private fun networkListener() {
        connectivityReceiverRegistering(true)
        App.instance.setConnectivityListener(this)
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
                intentFilter = IntentFilter("com.routesme.taxi_screen.SOME_ACTION")
                intentFilter.addAction(CONNECTIVITY_ACTION)
                mContext.registerReceiver(connectivityReceiver, intentFilter)
            } else {
                mContext.unregisterReceiver(connectivityReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun fetchContent(){
        dialog?.show()
        val contentViewModel: ContentViewModel by viewModels()
        contentViewModel.getContent(1,50,mContext).observe(activity as HomeActivity, Observer<ContentResponse> {
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
                        displayAdvertisements = DisplayAdvertisements(qRCodeCallback)
                        if (!images.isNullOrEmpty()) displayAdvertisements.displayAdvertisementBannerList(images,view1.advertisementsImageView)
                        if (!videos.isNullOrEmpty()) {  startPreLoadingService(videos); displayAdvertisements.displayAdvertisementVideoList(videos,view1.playerView,view1.videoRingProgressBar)}
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

    /*
    private fun fetchAdvertisementData() {

        val bannerList = mutableSetOf<Content>()
        val videoList = mutableSetOf<Content>()

        val model: RoutesViewModel by viewModels()
        model.getContent(mContext).observe((instance as LifecycleOwner), Observer<ContentResponse> {
            if (!it.data.isNullOrEmpty()){
                for (content in it.data){
                    when(content.type){
                        ContentType.Image.value -> bannerList.add(Content(content.content_id, content.url, content.qrCode))
                        else -> videoList.add(Content(content.content_id, content.url, content.qrCode))
                    }
                }

                displayAdvertisements = DisplayAdvertisements(qRCodeCallback)
                if (!bannerList.isNullOrEmpty()) displayAdvertisements.displayAdvertisementBannerList(bannerList.toList(),view1.advertisementsImageView)
                if (!videoList.isNullOrEmpty()) {  startPreLoadingService(videoList.toList()); displayAdvertisements.displayAdvertisementVideoList(videoList.toList(),view1.playerView,view1.videoRingProgressBar)}
            }
        })

        test()
        isDataFetched = true
    }

    private fun test(){
        val bannerList = mutableSetOf<Content>()
        val videoList = mutableSetOf<Content>()
        videoList.apply {
            add(Content(0,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c",QrCode("Macdonalds offers a 30% discount \n Scan Now!","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F1.png?alt=media&token=24e4ed47-e77f-489a-bb87-36955ba85b84")))
            add(Content(1,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3", QrCode("MAC Offer! Scan Now!","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F2.png?alt=media&token=071c6c0d-0959-4a5e-99fe-49b01eb21977")))
        }
        bannerList.apply {
            add(Content(0,"https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957"))
            add(Content(1,"https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",QrCode("Macdonalds offers a 30% discount \n Scan Now!","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F1.png?alt=media&token=24e4ed47-e77f-489a-bb87-36955ba85b84")))
        }
        displayAdvertisements = DisplayAdvertisements(qRCodeCallback)
        if (!bannerList.isNullOrEmpty()) displayAdvertisements.displayAdvertisementBannerList(bannerList.toList(),view1.advertisementsImageView)
        if (!videoList.isNullOrEmpty()) {  startPreLoadingService(videoList.toList()); displayAdvertisements.displayAdvertisementVideoList(videoList.toList(),view1.playerView,view1.videoRingProgressBar)}
    }
*/

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