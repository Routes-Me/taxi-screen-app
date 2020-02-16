package com.routesme.taxi_screen.kotlin.View.Home_Screen_Fragments

import android.app.Activity
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.routesme.taxi_screen.java.Class.App
import com.routesme.taxi_screen.java.Class.Operations
import com.routesme.taxi_screen.java.Detect_Network_Connection_Status.ConnectivityReceiver
import com.routesme.taxi_screen.java.Detect_Network_Connection_Status.ConnectivityReceiver.ConnectivityReceiverListener
import com.routesme.taxi_screen.kotlin.Model.BannerModel
import com.routesme.taxi_screen.kotlin.Model.ItemAnalytics
import com.routesme.taxi_screen.kotlin.Model.VideoModel
import com.routesme.taxiscreen.R
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import kotlinx.android.synthetic.main.content_fragment.view.*
import java.util.ArrayList

@Suppress("DEPRECATION")
class ContentFragment : Fragment() ,View.OnClickListener, ConnectivityReceiverListener{

    private lateinit var contentFragmentView: View
    private lateinit var Advertisement_Banner_CardView:CardView
    private lateinit var Advertisement_Video_CardView:CardView
    private lateinit var videoRingProgressBar:RingProgressBar
    private val sharedPreferences = context?.getSharedPreferences("userData", Activity.MODE_PRIVATE)
    private  var firebaseAnalytics = context?.let { FirebaseAnalytics.getInstance(it) }
    private val connectivityReceiver = ConnectivityReceiver()
    private lateinit var intentFilter: IntentFilter
    private var isConnected = false
    private var isDataFetched = false
    private lateinit var operations:Operations

    companion object {
        fun newInstance(): ContentFragment = ContentFragment()
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

    override fun onDestroy() {
        super.onDestroy()
        connectivityReceiverRegistering(false)
    }

    private fun initialize() {
        Advertisement_Banner_CardView = contentFragmentView.Advertisement_Banner_CardView
        Advertisement_Banner_CardView.setOnClickListener(this)
        Advertisement_Video_CardView = contentFragmentView.Advertisement_Video_CardView
        Advertisement_Video_CardView.setOnClickListener(this)
        videoRingProgressBar = contentFragmentView.videoRingProgressBar
        firebaseAnalyticsSetUp()
        operations = Operations(activity)
        checkConnection()
    }

    private fun firebaseAnalyticsSetUp() {
        val tabletSerialNumber = sharedPreferences?.getString("tabletSerialNo", null)
        firebaseAnalytics?.setUserId(tabletSerialNumber)
    }
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.Advertisement_Banner_CardView -> updateFirebaseAnalystics(ItemAnalytics(2, "click_banner"))
            R.id.Advertisement_Video_CardView ->  updateFirebaseAnalystics(ItemAnalytics(1, "click_video"))
        }
    }
    private fun updateFirebaseAnalystics(itemAnalytics: ItemAnalytics) {
        val params = Bundle()
        params.putInt("id",itemAnalytics.id)
        params.putString("name",itemAnalytics.name)
        itemAnalytics.name?.let { firebaseAnalytics?.logEvent(it, params) }
    }

    private fun checkConnection() {
        isConnected = ConnectivityReceiver.isConnected()
        if (isConnected) {
            fetchAdvertisementData()
          //  operations.fetchAdvertisementData()
            isDataFetched = true
        } else {
            networkListener()
        }
    }
    private fun networkListener() {
        connectivityReceiverRegistering(true)
        App.getInstance().setConnectivityListener(this)
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected && !isDataFetched) {
            fetchAdvertisementData()
            //operations.fetchAdvertisementData()
            isDataFetched = true
            connectivityReceiverRegistering(false)
        }
    }
    private fun connectivityReceiverRegistering(register: Boolean) {
        try { // register or unRegister your broadcast connectionReceiver
            if (register) {
                intentFilter = IntentFilter("com.routesme.taxi_screen.SOME_ACTION")
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                activity!!.registerReceiver(connectivityReceiver, intentFilter)
            } else {
                activity!!.unregisterReceiver(connectivityReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun fetchAdvertisementData(){
        val channdelId = channelId()
        val videoList = operations.advertisementVideoList(channdelId)
       // val bannerList = operations.advertisementBannerList(channdelId!!)
        Toast.makeText(activity,"videos size: ${videoList.size} ",Toast.LENGTH_SHORT).show()
    }

    private fun channelId() = sharedPreferences?.getInt("tabletChannelId", 0)


}