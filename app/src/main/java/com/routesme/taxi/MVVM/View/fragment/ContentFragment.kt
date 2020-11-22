package com.routesme.taxi.MVVM.View.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.routesme.taxi.Class.ConnectivityReceiver
import com.routesme.taxi.Class.Operations
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.ContentResponse
import com.routesme.taxi.MVVM.Model.ContentType
import com.routesme.taxi.MVVM.Model.Data
import com.routesme.taxi.MVVM.Model.Error
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.R
import dmax.dialog.SpotsDialog
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import kotlinx.android.synthetic.main.content_fragment.*
import kotlinx.android.synthetic.main.content_fragment.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException


class ContentFragment : Fragment(),SimpleExoPlayer.VideoListener {

    private lateinit var mContext: Context
    private lateinit var mView: View
    private val SEC:Long = 120
    private val MIL:Long = 1000
    private var count = 0
    private var connectivityReceiver: ConnectivityReceiver? = null
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
        /*
        try {
            qRCodeCallback = activity as QRCodeCallback
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement QRCodeCallback")
        }
         */
        super.onAttach(context)

    }

    override fun onDetach() {
      //  qRCodeCallback = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.content_fragment, container, false)
        requireActivity().registerReceiver(myReceiver, intentFilter);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        videoRingProgressBar = view.videoRingProgressBar
        timerHandler = Handler()
        videoShadow = view.videoShadow
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
        Log.d("Video","Start")
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
                    isDataFetched = true
                    val images = it.imageList.toList()
                    val videos = it.videoList.toList()
                    if(isAlive) removeThread()
                    if (images.isNullOrEmpty() && videos.isNullOrEmpty()){
                        Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.no_data_found))
                        return@Observer
                    }else{
                        if (!images.isNullOrEmpty()) AdvertisementsHelper.instance.displayImages(images, mView.advertisementsImageView)
                        AdvertisementsHelper.instance.displayVideos(mContext, videos, mView.playerView, mView.videoRingProgressBar)
                    }

                } else {

                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let {
                            startThread()
                            //errors -> displayErrors(errors)
                             }
                    } else if (it.mThrowable != null) {
                        if (it.mThrowable is IOException) {
                            startThread()
                            //Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.network_Issue))
                        } else {
                            startThread()
                            //Operations.instance.displayAlertDialog(mContext, getString(R.string.content_error_title), getString(R.string.conversion_Issue))
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

    private fun startThread(){

        EventBus.getDefault().post(DemoVideo(true))
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
        EventBus.getDefault().post(DemoVideo(false))

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