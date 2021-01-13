package com.routesme.taxi.MVVM.View.fragment

import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import carbon.widget.RelativeLayout
import com.google.android.exoplayer2.SimpleExoPlayer
import com.routesme.taxi.Class.AdvertisementsHelper
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.Class.SideFragmentAdapter.ImageViewPager
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.R
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.utils.Session
import dmax.dialog.SpotsDialog
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import kotlinx.android.synthetic.main.content_fragment.view.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException
import java.lang.Runnable


class ContentFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var mView: View
    //private var sharedPreferences: SharedPreferences? = null
    //private var editor: SharedPreferences.Editor? = null
    private var device_id : Int = 0
    private val SEC:Long = 300
    private val MIL:Long = 1000
    //private var isDataFetched = false
    private var dialog: SpotsDialog? = null
    private var videoRingProgressBar: RingProgressBar? = null
    private var isAlive = false
    private var videoShadow: RelativeLayout? = null
    private var session:Session?=null
    //val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
    private lateinit var  displayImageJob : Job
    private lateinit var  videoProgressJob : Job
    private lateinit var  callApiJob : Job

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.content_fragment, container, false)

        return view
    }

    override fun onDestroyView() {

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        videoRingProgressBar = view.videoRingProgressBar
        session = Session(this.requireActivity())
        videoShadow = view.videoShadow
        displayImageJob = Job()
        videoProgressJob = Job()
        callApiJob = Job()
        //sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        //editor= sharedPreferences?.edit()
        device_id = session?.deviceId()!!.toInt()
        fetchContent()
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroy() {
        AdvertisementsHelper.instance.release()
        displayImageJob?.cancel()
        videoProgressJob?.cancel()
        callApiJob?.cancel()
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
                        if(isAlive) removeThread()
                        if (!images.isNullOrEmpty()) AdvertisementsHelper.instance.displayImages(mContext, images, mView.advertisementsImageView, mView.advertisementsImageView2, displayImageJob)
                        videoProgressJob?.let { coroutineProgressJob->
                            AdvertisementsHelper.instance.configuringMediaPlayer(mContext, videos, mView.playerView, mView.videoRingProgressBar,mView.Advertisement_Video_CardView,mView.bgImage,coroutineProgressJob)
                        }

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
        isAlive = true
        EventBus.getDefault().post(DemoVideo(true,errorMessage))
        callApiJob?.let {
            CoroutineScope(Dispatchers.Main + it).launch {
                delay(SEC*MIL)
                fetchContent()

            }
        }
    }

    private fun removeThread(){

        if(callApiJob!!.isActive) callApiJob?.cancelChildren()

        isAlive=false
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