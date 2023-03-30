package com.routesme.vehicles.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.routesme.vehicles.App
import com.routesme.vehicles.R
import com.routesme.vehicles.api.Constants
import com.routesme.vehicles.data.model.ContentResponse
import com.routesme.vehicles.data.model.Data
import com.routesme.vehicles.helper.AdminConsoleHelper
import com.routesme.vehicles.helper.DateHelper
import com.routesme.vehicles.helper.DateOperations
import com.routesme.vehicles.helper.SharedPreferencesHelper
import com.routesme.vehicles.room.AdvertisementDatabase
import com.routesme.vehicles.room.factory.ViewModelFactory
import com.routesme.vehicles.room.helper.DatabaseHelperImpl
import com.routesme.vehicles.room.viewmodel.RoomDBViewModel
import com.routesme.vehicles.service.VideoService
import com.routesme.vehicles.view.adapter.BottomBannerAdapter
import com.routesme.vehicles.view.adapter.ImageBannerAdapter
import com.routesme.vehicles.view.adapter.WifiAndQRCodeAdapter
import com.routesme.vehicles.view.events.AnimateVideo
import com.routesme.vehicles.view.events.DemoVideo
import com.routesme.vehicles.view.utils.Type
import com.routesme.vehicles.viewmodel.ContentViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.taxii.content_fragment.*
import kotlinx.android.synthetic.taxii.content_fragment.view.*
import kotlinx.android.synthetic.taxii.date_cell.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ContentFragment : Fragment(), CoroutineScope by MainScope() {
    private val SEND_ANALYTICS_REPORT = "SEND_ANALYTICS_REPORT"
    private lateinit var mContext: Context
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var device_id: String = ""
    private var vehiclePlateNumber: String? = null
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
    private lateinit var contentFragmentView: View
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentFragmentView  = inflater.inflate(R.layout.content_fragment, container, false)
        return contentFragmentView
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        callApiJob = Job()
        sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        glide = Glide.with(App.instance)
        imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!
        vehiclePlateNumber = sharedPreferences?.getString(SharedPreferencesHelper.vehicle_plate_number, null)
        viewModel = ViewModelProvider(this, ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext)))).get(RoomDBViewModel::class.java)
        contentViewModel = ViewModelProvider(this.requireActivity()).get(ContentViewModel::class.java)
        dbHelper = DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext))
        workManager.enqueueUniquePeriodicWork(SEND_ANALYTICS_REPORT, ExistingPeriodicWorkPolicy.KEEP, App.periodicWorkRequest)
        fetchContent()

        generateSharingLink(
               // deepLink = "${Constants.FirebaseGoRoutesAppDomainPrefix}/vehicles?plateNumber=$vehiclePlateNumber".toUri(),
                deepLink = "${Constants.FirebaseGoRoutesAppDomainPrefix}/vehicles?plateNumber=$vehiclePlateNumber".toUri(),
                previewImageLink = null //post.image.toUri()
        ) { generatedLink ->
            // Use this generated Link to share via Intent
           // Log.d("GoRoutesAppQRCode","Link: $generatedLink")
        }

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
        sharedPreferences?.let {
            val institutionId = it.getString(SharedPreferencesHelper.institution_id, null)
            val vehicleId = it.getString(SharedPreferencesHelper.vehicle_id, null)
            val plateNumber = it.getString(SharedPreferencesHelper.vehicle_plate_number, null)
            if (!institutionId.isNullOrEmpty() && !vehicleId.isNullOrEmpty() && !plateNumber.isNullOrEmpty()){
                contentViewModel.getContent(1, 100, institutionId, vehicleId, plateNumber, mContext)?.observe(viewLifecycleOwner, Observer<ContentResponse> {
                    dialog?.dismiss()
                    if (it != null) {
                        if (it.isSuccess) {
                            val images = it.imageList.toList()
                            val videos = it.videoList
                            if (images.isNullOrEmpty() && videos.isNullOrEmpty()) {
                                startThread(getString(R.string.no_data_found))
                                return@Observer
                            } else {
                                if (isAlive) removeThread()
                                contentFragmentView.constraintLayoutDateCell?.let {
                                    it.visibility = View.VISIBLE
                                }
                                launch {
                                    setTime()
                                    if (!images.isNullOrEmpty()) {
                                        setUpImageAdapter(images)
                                        setUpWifiAndQRCodeAdapter(images)
                                    }
                                    if (!videos.isNullOrEmpty()) {
                                        setUpAdapter(videos)
                                        startVideoService(videos)
                                    }
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
        }
    }

    private fun setUpWifiAndQRCodeAdapter(list: List<Data>) {
        wifiAndQRCodeAdapter = WifiAndQRCodeAdapter(mContext, list)
        contentFragmentView.bottomRightPromotion.apply {
            adapter = wifiAndQRCodeAdapter
            isUserInputEnabled = false
        }
    }

    private fun setUpAdapter(list: List<Data>) {
        bottomBannerAdapter = BottomBannerAdapter(mContext, list)
        contentFragmentView.bottomLeftPromtion.apply {
            adapter = bottomBannerAdapter
            isUserInputEnabled = false
        }
    }

    private fun setUpImageAdapter(images: List<Data>) {
        imageBannerAdapter = ImageBannerAdapter(mContext, images)
        contentFragmentView.viewPageSideBanner.apply {
            adapter = imageBannerAdapter
            isUserInputEnabled = false
        }
        launch {
            while (isActive) {
                val image =  images[count]
                image.contentId?.let {
                    viewModel.insertLog(it, image.resourceNumber!!, DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(), Type.IMAGE.media_type)
                }
                contentFragmentView.viewPageSideBanner.setCurrentItem(count, true)
                contentFragmentView.bottomRightPromotion.setCurrentItem(count, true)
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
                contentFragmentView.bottomLeftPromtion.setCurrentItem(position, true)
            }

        } catch (e: Exception) {

        }
    }

    private fun videoProgressbarRunnable() {
        launch {
            while (isActive) {
                if (contentFragmentView.playerView.player != null) {
                    val current = (contentFragmentView.playerView.player?.currentPosition)!!.toInt()
                    val progress = current * 100 / (contentFragmentView.playerView.player?.duration)!!.toInt()
                    contentFragmentView.videoRingProgressBar?.progress = progress
                    delay(1000)
                }
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

    private fun startVideoService(list: MutableList<Data>) {
        val intent = Intent(mContext, VideoService::class.java)
        intent.putExtra("video_list", list as ArrayList<Data>)
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        callApiJob.cancel()
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
                contentFragmentView.playerView.player = service.getExoPlayerInstance()
                contentFragmentView.bottomLeftPromtion.setCurrentItem(contentFragmentView.playerView.player?.currentPeriodIndex!!, true)
                videoProgressbarRunnable()
            }
        }

    }

    private fun generateSharingLink(deepLink: Uri, previewImageLink: Uri?, getShareableLink: (String) -> Unit = {}) {

        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            // What is this link parameter? You will get to know when we will actually use this function.
            link = deepLink

            // [domainUriPrefix] will be the domain name you added when setting up Dynamic Links at Firebase Console.
            // You can find it in the Dynamic Links dashboard.
            domainUriPrefix = Constants.FirebaseGoRoutesAppDomainPrefix

            // Pass your preview Image Link here;
            previewImageLink?.let { setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder().setImageUrl(it).build()) }

            // Required
            setAndroidParameters(DynamicLink.AndroidParameters.Builder(Constants.GoRoutesApp_AndroidPackageName).build())

            // Finally
            buildShortDynamicLink()
        }.also {
            it.addOnSuccessListener { dynamicLink ->
                // This lambda will be triggered when short link generation is successful

                // Retrieve the newly created dynamic link so that we can use it further for sharing via Intent.
                Log.d("GoRoutesAppQRCode","Successfully... Link: $dynamicLink")
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
            it.addOnFailureListener {
                // This lambda will be triggered when short link generation failed due to an exception
                Log.d("GoRoutesAppQRCode","Failure... Exception: $it")
                // Handle
            }
        }
    }
}