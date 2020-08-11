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
import com.routesme.taxi_screen.kotlin.Model.BannerModel
import com.routesme.taxi_screen.kotlin.Model.ItemAnalytics
import com.routesme.taxi_screen.kotlin.Model.VideoModel
import com.routesme.taxi_screen.kotlin.VideoPlayer.Constants
import com.routesme.taxi_screen.kotlin.VideoPlayer.VideoPreLoadingService
import com.routesme.taxi_screen.kotlin.ViewModel.RoutesViewModel
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.content_fragment.view.*

class ContentFragment : Fragment(), View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var tabletSerialNumber:String
    private lateinit var contentFragmentContext: Context
    private lateinit var view1: View
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val connectivityReceiver = ConnectivityReceiver()
    private lateinit var intentFilter: IntentFilter
    private var isConnected = false
    private var isDataFetched = false
    private val displayAdvertisements = DisplayAdvertisements.instance

    companion object {
        val instance = ContentFragment()
    }

    override fun onAttach(context: Context) {
        contentFragmentContext = context
        sharedPreferences = context.getSharedPreferences("userData", Activity.MODE_PRIVATE)
       // tabletSerialNumber = this.sharedPreferences.getString("tabletSerialNo", null)
       // firebaseAnalytics = FirebaseAnalytics.getInstance(context)
       // firebaseAnalytics.setUserId(tabletSerialNumber)
        super.onAttach(context)
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
           // playAds()
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
           // playAds()
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
    private fun playAds(){
        val videos = mutableListOf<VideoModel>().apply {
            add(VideoModel(0,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c"))
            add(VideoModel(1,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3"))
            // add(VideoModel(2,"https://drive.google.com/file/d/1EMfDMeQH4UUPg1n0JKp4sl3VX6HHTRLz/view?usp=sharing"))
        }
        displayAdvertisements.displayAdvertisementVideoList(videos,view1.playerView,view1.videoRingProgressBar)
        val images = mutableListOf<BannerModel>().apply {
            add(BannerModel(0,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FPepsiadvertisingth_1548685296614-HR.jpg?alt=media&token=5f05924f-774f-4b96-a7f2-69626959b8e8"))
            add(BannerModel(1,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FAdver--Banners--150x750px-RIOT.jpg?alt=media&token=85154f31-7e4e-4204-acd0-f0153b7eccb3"))
            add(BannerModel(2,"https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2F058ca55eae5b86fa8a4d52c1d1e5a4a4.jpg?alt=media&token=e2197a09-df37-4556-a703-f864d0ce6cf2"))
        }
        displayAdvertisements.displayAdvertisementBannerList(images,view1.advertisementsImageView)
    }

    private fun fetchAdvertisementData() {
        fetchBannerList()
        fetchVideoList()
        isDataFetched = true
    }

    private fun fetchBannerList() {
        val model: RoutesViewModel by viewModels()
            model.getBannerList(channelId(), contentFragmentContext)?.observe((instance as LifecycleOwner), Observer<List<BannerModel>> {

                displayAdvertisements.displayAdvertisementBannerList(it,view1.advertisementsImageView)
            })
    }

    private fun fetchVideoList() {
        val model: RoutesViewModel by viewModels()
            model.getVideoList(channelId(), contentFragmentContext)?.observe((instance as LifecycleOwner), Observer<List<VideoModel>> {
                startPreLoadingService(it)
                displayAdvertisements.displayAdvertisementVideoList(it,view1.playerView,view1.videoRingProgressBar)
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