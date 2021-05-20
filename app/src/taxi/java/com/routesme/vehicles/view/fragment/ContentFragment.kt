package com.routesme.vehicles.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.routesme.vehicles.data.model.ContentResponse
import com.routesme.vehicles.data.model.Data
import com.routesme.vehicles.R
import com.routesme.vehicles.helper.AdvertisementsHelper
import com.routesme.vehicles.helper.DateHelper
import com.routesme.vehicles.helper.DateOperations
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.room.AdvertisementDatabase
import com.routesme.vehicles.room.factory.ViewModelFactory
import com.routesme.vehicles.room.helper.DatabaseHelperImpl
import com.routesme.vehicles.room.viewmodel.RoomDBViewModel
import com.routesme.vehicles.App
import com.routesme.vehicles.service.VideoService
import com.routesme.vehicles.view.adapter.BottomBannerAdapter
import com.routesme.vehicles.view.adapter.ImageBannerAdapter
import com.routesme.vehicles.view.adapter.WifiAndQRCodeAdapter
import com.routesme.vehicles.view.events.AnimateVideo
import com.routesme.vehicles.view.events.DemoVideo
import com.routesme.vehicles.view.utils.Type
import com.routesme.vehicles.viewmodel.ContentViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.taxi.content_fragment.*
import kotlinx.android.synthetic.taxi.date_cell.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*

class ContentFragment : Fragment(), CoroutineScope by MainScope() {
    private val SEND_ANALYTICS_REPORT = "SEND_ANALYTICS_REPORT"
    private lateinit var mContext: Context
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var device_id: String = ""
    private val SEC: Long = 30
    private var position = 0
    private val MIL: Long = 1000
    private var count = 0
    private var dialog: SpotsDialog? = null
    private var isAlive = false
    private val dateOperations = DateOperations.instance
    private lateinit var callApiJob: Job
    private var bottomBannerAdapter: BottomBannerAdapter? = null
    private var wifiAndQRCodeAdapter: WifiAndQRCodeAdapter? = null
    private var imageBannerAdapter: ImageBannerAdapter? = null
    private lateinit var viewModel: RoomDBViewModel
    private var date = Date()
    private lateinit var dbHelper : DatabaseHelperImpl
    private lateinit var glide: RequestManager
    private lateinit var contentViewModel: ContentViewModel
    private lateinit var imageOptions: RequestOptions
    private var workManager = WorkManager.getInstance()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.content_fragment, container, false)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        callApiJob = Job()
        sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        glide = Glide.with(App.instance)
        imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!
        viewModel = ViewModelProvider(this, ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext)))).get(RoomDBViewModel::class.java)
        contentViewModel = ViewModelProvider(this.requireActivity()).get(ContentViewModel::class.java)
        dbHelper = DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext))
        workManager.enqueueUniquePeriodicWork(SEND_ANALYTICS_REPORT, ExistingPeriodicWorkPolicy.KEEP, App.periodicWorkRequest)
        fetchContent()
    }

    @SuppressLint("SetTextI18n")
    private fun setTime() {

        launch {
            while (isActive){
                date = Date()
                clockTv.text = dateOperations.timeClock(date)
                dayTv.text = "${dateOperations.dayOfWeek(date)} \n ${dateOperations.date(date)}"
                delay(60 * 1000)
            }
        }
    }

    private fun fetchContent() {
        contentViewModel.getContent(1, 100, mContext)?.observe(viewLifecycleOwner, Observer<ContentResponse> {
            dialog?.dismiss()
            if (it != null) {
                if (it.isSuccess) {
                    val images = it.imageList.toList()
                    val videos = it.videoList.toList()
                    if (images.isNullOrEmpty() && videos.isNullOrEmpty()) {
                        startThread(getString(R.string.no_data_found))
                        return@Observer
                    } else {
                        if (isAlive) removeThread()
                        constraintLayoutDateCell?.let {
                            it.visibility = View.VISIBLE
                        }
                        launch {
                            setTime()
                            if (!images.isNullOrEmpty()) {
                                setUpImageAdapter(images)
                                setUpWifiAndQRCodeAdapter(images)
                            }
                            setUpAdapter(videos)
                            startVideoService(videos)
                        }
                    }
                } else {

                    if (!it.mResponseErrors?.errors.isNullOrEmpty()) {
                        it.mResponseErrors?.errors?.let {
                            startThread(getString(R.string.no_data_found))
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

    private fun setUpWifiAndQRCodeAdapter(list: List<Data>) {
        wifiAndQRCodeAdapter = WifiAndQRCodeAdapter(mContext, list)
        bottomRightPromotion.apply {
            adapter = wifiAndQRCodeAdapter
            isUserInputEnabled = false
        }
    }

    private fun setUpAdapter(list: List<Data>) {
        bottomBannerAdapter = BottomBannerAdapter(mContext, list)
        bottomLeftPromtion.apply {
            adapter = bottomBannerAdapter
            isUserInputEnabled = false
        }
    }

    private fun setUpImageAdapter(images: List<Data>) {
        imageBannerAdapter = ImageBannerAdapter(mContext, images)
        viewPageSideBanner.apply {
            adapter = imageBannerAdapter
            isUserInputEnabled = false
        }
        launch {
            while (isActive) {
               /* dbHelper.getList().forEach {
                    Log.d("AnalyticsTesting","${it.id},${it.resourceNumber},${it.date},${it.time_in_day},${it.advertisementId},${it.morning},${it.noon},${it.evening},${it.night},")
                }*/
                val image =  images[count]
                image.contentId?.let {
                    viewModel.insertLog(it, image.resourceNumber!!, DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(), Type.IMAGE.media_type)
                }
                viewPageSideBanner.setCurrentItem(count, true)
                bottomRightPromotion.setCurrentItem(count, true)
                if (imageBannerAdapter?.itemCount!! - 1 === count) count = 0 else count++
                delay(15 * 1000)
            }
        }
    }


    private fun removeThread() {
        if (callApiJob.isActive) callApiJob.cancelChildren()
        isAlive = false
        EventBus.getDefault().post(DemoVideo(false, ""))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(animateVideo: AnimateVideo) {
        try {
            launch {
                position = animateVideo.position
                bottomLeftPromtion.setCurrentItem(position, true)
            }

        } catch (e: Exception) {

        }
    }

    private fun videoProgressbarRunnable() {
        launch {
            while (isActive) {
                val current = (playerView.player?.currentPosition)!!.toInt()
                val progress = current * 100 / (playerView.player?.duration)!!.toInt()
                videoRingProgressBar?.progress = progress
                delay(1000)
            }
        }
    }

    private fun startThread(errorMessage: String) {
        isAlive = true
        EventBus.getDefault().post(DemoVideo(true, errorMessage))
        CoroutineScope(Dispatchers.Main + callApiJob).launch {
            delay(SEC * MIL)
            fetchContent()

        }
    }

    private fun startVideoService(list: List<Data>) {
        val intent = Intent(mContext, VideoService::class.java)
        intent.putExtra("video_list", list as ArrayList<Data>)
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)

    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        Log.d("AnalyticsTesting","Destroy")
        callApiJob.cancel()
        AdvertisementsHelper.instance.deleteCache()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is VideoService.VideoServiceBinder) {
                playerView.player = service.getExoPlayerInstance()
                bottomLeftPromtion.setCurrentItem(playerView.player?.currentPeriodIndex!!, true)
                videoProgressbarRunnable()
            }
        }

    }

}