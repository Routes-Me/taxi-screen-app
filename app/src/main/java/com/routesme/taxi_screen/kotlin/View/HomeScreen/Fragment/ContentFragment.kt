package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.ConnectivityReceiver
import com.routesme.taxi_screen.kotlin.Class.DisplayAdvertisements
import com.routesme.taxi_screen.kotlin.Model.*
import com.routesme.taxi_screen.kotlin.VideoPlayer.Constants
import com.routesme.taxi_screen.kotlin.VideoPlayer.VideoPreLoadingService
import com.routesme.taxi_screen.kotlin.ViewModel.RoutesViewModel
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.content_fragment.view.*

class ContentFragment : Fragment(), View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var tabletSerialNumber:String
    private lateinit var contentFragmentContext: Context
    private var qRCodeCallback: QRCodeCallback? = null
    private lateinit var view1: View
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val connectivityReceiver = ConnectivityReceiver()
    private lateinit var intentFilter: IntentFilter
    private var isConnected = false
    private var isDataFetched = false
    private lateinit var displayAdvertisements: DisplayAdvertisements

    companion object {
        val instance = ContentFragment()
    }

    override fun onAttach(context: Context) {
        contentFragmentContext = context
        sharedPreferences = context.getSharedPreferences("userData", Activity.MODE_PRIVATE)
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
            fetchAdvertisementData()
            //testingPlayAds()
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
            fetchAdvertisementData()
            //testingPlayAds()
            connectivityReceiverRegistering(false)
        }
    }

    private fun connectivityReceiverRegistering(register: Boolean) {
        try {
            if (register) {
                intentFilter = IntentFilter("com.routesme.taxi_screen.SOME_ACTION")
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                contentFragmentContext.registerReceiver(connectivityReceiver, intentFilter)
            } else {
                contentFragmentContext.unregisterReceiver(connectivityReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    //for test..
    private fun testingPlayAds(){
        val videos = mutableListOf<VideoModel>().apply {
            add(VideoModel(0,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c"))
            add(VideoModel(1,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3"))
            // add(VideoModel(2,"https://drive.google.com/file/d/1EMfDMeQH4UUPg1n0JKp4sl3VX6HHTRLz/view?usp=sharing"))
        }
       // displayAdvertisements.displayAdvertisementVideoList(videos,view1.playerView,view1.videoRingProgressBar)
        val images = mutableListOf<BannerModel>().apply {
            add(BannerModel(0,"https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957"))
            add(BannerModel(1,"https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5"))
        }
       // displayAdvertisements.displayAdvertisementBannerList(images,view1.advertisementsImageView)
    }

    private fun fetchAdvertisementData() {

        val bannerList = mutableSetOf<Content>()
        val videoList = mutableSetOf<Content>()

        val model: RoutesViewModel by viewModels()
        model.getContent(contentFragmentContext).observe((instance as LifecycleOwner), Observer<ContentResponse> {
            if (!it.data.isNullOrEmpty()){
              for (content in it.data){
                  when(content.type){
                      ContentType.Image.value -> bannerList.add(Content(content.content_id, content.url, content.qrCode))
                      else -> videoList.add(Content(content.content_id, content.url, content.qrCode))
                  }
              }

                displayAdvertisements = DisplayAdvertisements(qRCodeCallback)
                if (!bannerList.isNullOrEmpty()) displayAdvertisements.displayAdvertisementBannerList(bannerList.toList(),view1.advertisementsImageView)
                if (!videoList.isNullOrEmpty()) displayAdvertisements.displayAdvertisementVideoList(videoList.toList(),view1.playerView,view1.videoRingProgressBar)
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
        if (!videoList.isNullOrEmpty()) displayAdvertisements.displayAdvertisementVideoList(videoList.toList(),view1.playerView,view1.videoRingProgressBar)
    }

    private fun fetchBannerList() {
        val model: RoutesViewModel by viewModels()
            model.getBannerList(channelId(), contentFragmentContext)?.observe((instance as LifecycleOwner), Observer<List<BannerModel>> {

              //  displayAdvertisements.displayAdvertisementBannerList(it,view1.advertisementsImageView)
            })
    }

    private fun fetchVideoList() {
        val model: RoutesViewModel by viewModels()
            model.getVideoList(channelId(), contentFragmentContext)?.observe((instance as LifecycleOwner), Observer<List<VideoModel>> {
                startPreLoadingService(it)
                //displayAdvertisements.displayAdvertisementVideoList(it,view1.playerView,view1.videoRingProgressBar)
            })
    }

    private fun startPreLoadingService(it: List<VideoModel>) {
        val videoList = ArrayList<String>()
        for (video in it){
            videoList.add(video.advertisement_URL.toString())
        }
        val preloadingServiceIntent = Intent(contentFragmentContext, VideoPreLoadingService::class.java)
        preloadingServiceIntent.putStringArrayListExtra(Constants.VIDEO_LIST, videoList)
        context?.startService(preloadingServiceIntent)
    }

    private fun channelId() = sharedPreferences.getInt("tabletChannelId", 0)
}