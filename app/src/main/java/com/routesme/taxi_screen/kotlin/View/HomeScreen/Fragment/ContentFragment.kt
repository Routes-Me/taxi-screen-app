package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.app.Activity
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
import com.routesme.taxi_screen.kotlin.ViewModel.RoutesViewModel
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.content_fragment.view.*

class ContentFragment : Fragment(), View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var contentFragmentView: View
    private lateinit var sharedPreferences: SharedPreferences
    private var firebaseAnalytics = context?.let { FirebaseAnalytics.getInstance(it) }
    private val connectivityReceiver = ConnectivityReceiver()
    private lateinit var intentFilter: IntentFilter
    private var isConnected = false
    private var isDataFetched = false
    private lateinit var displayAdvertisements: DisplayAdvertisements

    companion object {
        private fun initAdvertiseViews(contentFragment: ContentFragment) {
            contentFragment.contentFragmentView.Advertisement_Banner_CardView.setOnClickListener(contentFragment)
            contentFragment.contentFragmentView.Advertisement_Video_CardView.setOnClickListener(contentFragment)
            val videoRingProgressBar = contentFragment.contentFragmentView.videoRingProgressBar
            val advertisementsVideoView = contentFragment.contentFragmentView.advertisementsVideoView
            val advertisementsImageView = contentFragment.contentFragmentView.advertisementsImageView
            contentFragment.displayAdvertisements = DisplayAdvertisements(contentFragment.activity, videoRingProgressBar, advertisementsVideoView, advertisementsImageView)
        }

        private fun firebaseAnalyticsSetUp(contentFragment: ContentFragment) {
            val tabletSerialNumber = contentFragment.sharedPreferences.getString("tabletSerialNo", null)
            contentFragment.firebaseAnalytics?.setUserId(tabletSerialNumber)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contentFragmentView = inflater.inflate(R.layout.content_fragment, container, false)

        initialize()

        return contentFragmentView
    }

    override fun onPause() {
        super.onPause()
        connectivityReceiverRegistering(false)
    }

    private fun initialize() {
        sharedPreferences = context!!.getSharedPreferences("userData", Activity.MODE_PRIVATE)
        initAdvertiseViews(this)
        firebaseAnalyticsSetUp(this)
        checkConnection()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.Advertisement_Banner_CardView -> updateFirebaseAnalystics(ItemAnalytics(2, "click_banner"))
            R.id.Advertisement_Video_CardView -> updateFirebaseAnalystics(ItemAnalytics(1, "click_video"))
        }
    }

    private fun updateFirebaseAnalystics(itemAnalytics: ItemAnalytics) {
        val params = Bundle()
        params.putInt("id", itemAnalytics.id)
        params.putString("name", itemAnalytics.name)
        itemAnalytics.name?.let { firebaseAnalytics?.logEvent(it, params) }
    }

    private fun checkConnection() {
        isConnected = ConnectivityReceiver.isConnected
        if (isConnected) {
            fetchAdvertisementData()
            isDataFetched = true
        } else {
            networkListener()
        }
    }

    private fun networkListener() {
        connectivityReceiverRegistering(true)
        App.instance?.setConnectivityListener(this)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected && !isDataFetched) {
            fetchAdvertisementData()
            isDataFetched = true
            connectivityReceiverRegistering(false)
        }
    }

    private fun connectivityReceiverRegistering(register: Boolean) {
        try {
            if (register) {
                intentFilter = IntentFilter("com.routesme.taxi_screen.SOME_ACTION")
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                requireActivity().registerReceiver(connectivityReceiver, intentFilter)
            } else {
                requireActivity().unregisterReceiver(connectivityReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun fetchAdvertisementData() {
        fetchVideoList()
        fetchBannerList()
    }

    private fun fetchBannerList() {
        val model: RoutesViewModel by viewModels()
        context?.let {
            model.getBannerList(channelId(), it)?.observe((it as LifecycleOwner?)!!, Observer<List<BannerModel>> {
                displayAdvertisements.displayAdvertisementBannerList(it)
            })
        }
    }

    private fun fetchVideoList() {
        val model: RoutesViewModel by viewModels()
        context?.let {
            model.getVideoList(channelId(), it)?.observe((it as LifecycleOwner?)!!, Observer<List<VideoModel>> {
                displayAdvertisements.displayAdvertisementVideoList(it)
            })
        }
    }

    private fun channelId() = sharedPreferences.getInt("tabletChannelId", 0)
}