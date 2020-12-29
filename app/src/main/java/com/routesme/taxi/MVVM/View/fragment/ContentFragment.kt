package com.routesme.taxi.MVVM.View.fragment

import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import carbon.widget.RelativeLayout
import com.google.android.exoplayer2.SimpleExoPlayer
import com.routesme.taxi.Class.AdvertisementsHelper
import com.routesme.taxi.Class.SideFragmentAdapter.ImageViewPager
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.R
import com.routesme.taxi.helper.SharedPreferencesHelper
import dmax.dialog.SpotsDialog
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import kotlinx.android.synthetic.main.content_fragment.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException


class ContentFragment : Fragment(),SimpleExoPlayer.VideoListener {

    private lateinit var mContext: Context
    private lateinit var mView: View
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var pager: Int = 1
    private var device_id : Int = 0
    private val SEC:Long = 300
    private val MIL:Long = 1000
    var delay = 15 * 1000
    private var imageViewPagerAdapter:ImageViewPager?=null
    private var isDataFetched = false
    private var dialog: SpotsDialog? = null
    private var videoRingProgressBar: RingProgressBar? = null
    private var timerRunnable: Runnable? = null
    private var isAlive = false
    private var timerHandler: Handler? = null
    private var videoShadow: RelativeLayout? = null
    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0
    val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

    override fun onAttach(context: Context) {
        mContext = context

        super.onAttach(context)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.content_fragment, container, false)
        requireActivity().registerReceiver(myReceiver, intentFilter);
        return view
    }

    override fun onDestroyView() {
        requireActivity().unregisterReceiver(myReceiver)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        videoRingProgressBar = view.videoRingProgressBar
        timerHandler = Handler()
        videoShadow = view.videoShadow
        sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!.toInt()
        super.onViewCreated(view, savedInstanceState)
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = getConnectivityStatus(context)
            if (status != TYPE_NOT_CONNECTED && !isDataFetched) {
                fetchContent()
            }
        }
    }

    private fun getConnectivityStatus(context: Context): Int {
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

    override fun onStart() {

        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {

        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun fetchContent(){


        val contentViewModel: ContentViewModel by viewModels()
        contentViewModel.getContent(1,100,mContext).observe(viewLifecycleOwner , Observer<ContentResponse> {
            dialog?.dismiss()
            if (it != null) {
                if (it.isSuccess) {

                    val images = it.imageList.toList()
                    val videos = it.videoList.toList()
                    if (images.isNullOrEmpty() && videos.isNullOrEmpty()){
                        startThread(getString(R.string.no_data_found))

                        return@Observer
                    }else{
                        isDataFetched = true
                        if(isAlive) removeThread()
                        if (!images.isNullOrEmpty()) AdvertisementsHelper.instance.displayImages(mContext,images, mView.advertisementsImageView,mView.advertisementsImageView2,device_id)
                        AdvertisementsHelper.instance.displayVideos(mContext, videos, mView.playerView, mView.videoRingProgressBar,mView.Advertisement_Video_CardView,mView.bgImage,device_id)
                    }

                } else {

                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let {
                            startThread(getString(R.string.no_data_found))
                            //errors -> displayErrors(errors)
                             }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            startThread(getString(R.string.network_Issue))

                        } else {
                            startThread(getString(R.string.conversion_Issue))

                        }
                    }
                }
            } else {
                startThread(getString(R.string.unknown_error))

            }
        })
    }

    /*private fun displayErrors(errors: List<Error>) {
        for (error in errors) {
            Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), "Error message: ${error.detail}")
        }
    }*/

    private fun startThread(errorMessage:String){

        EventBus.getDefault().post(DemoVideo(true,errorMessage))
        isAlive = true
        timerRunnable = object : Runnable {
            override fun run() {

                timerHandler!!.postDelayed({

                    fetchContent()

                },SEC*MIL)
            }
        }
        timerHandler!!.post(timerRunnable)

    }

    private fun removeThread(){

        timerHandler!!.removeCallbacks(timerRunnable)
        EventBus.getDefault().post(DemoVideo(false,""))

    }

    @Subscribe()
    fun onEvent(data: Data){
        if (data.type ==  ContentType.Video.value){
            changeVideoCardColor(data.tintColor)
        }
    }

    private fun changeVideoCardColor(tintColor: Int?) {
        val color = ThemeColor(tintColor).getColor()
        val lowOpacityColor = ColorUtils.setAlphaComponent(color,33)
        videoShadow?.setElevationShadowColor(color)
        videoRingProgressBar?.let {
            it.ringColor = lowOpacityColor
            it.ringProgressColor = color
        }
    }
}