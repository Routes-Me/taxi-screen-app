package com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment

import android.app.Activity
import android.content.Context
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
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Activity.HomeScreen
import com.routesme.taxi_screen.kotlin.ViewModel.RoutesViewModel
import com.routesme.taxiscreen.R
import kotlinx.android.synthetic.main.content_fragment.view.*

class ContentFragment : Fragment(), View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

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
        val tabletSerialNumber = this.sharedPreferences.getString("tabletSerialNo", null)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.setUserId(tabletSerialNumber)
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
            R.id.Advertisement_Banner_CardView -> updateFirebaseAnalystics(ItemAnalytics(2, "click_banner"))
            R.id.Advertisement_Video_CardView -> updateFirebaseAnalystics(ItemAnalytics(1, "click_video"))
        }
    }

    private fun updateFirebaseAnalystics(itemAnalytics: ItemAnalytics) {
        val params = Bundle()
        params.putInt("id", itemAnalytics.id)
        params.putString("name", itemAnalytics.name)
        firebaseAnalytics.logEvent(itemAnalytics.name, params)
    }

    private fun checkConnection() {
        isConnected = ConnectivityReceiver.isConnected
        if (isConnected) {
            fetchAdvertisementData()
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

                displayAdvertisements.displayAdvertisementVideoList(it,view1.advertisementsVideoView,view1.videoRingProgressBar)
            })
    }

    private fun channelId() = sharedPreferences.getInt("tabletChannelId", 0)
}