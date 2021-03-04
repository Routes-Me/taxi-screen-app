package com.routesme.taxi.view.fragment

import android.animation.ObjectAnimator
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.data.model.ContentResponse
import com.routesme.taxi.data.model.Data
import com.routesme.taxi.data.model.ReportResponse
import com.routesme.taxi.viewmodel.ContentViewModel
import com.routesme.taxi.view.events.AnimateVideo
import com.routesme.taxi.view.events.DemoVideo
import com.routesme.taxi.service.VideoService
import com.routesme.taxi.R
import com.routesme.taxi.helper.*
import com.routesme.taxi.room.ResponseBody
import com.routesme.taxi.room.AdvertisementDatabase
import com.routesme.taxi.room.entity.AdvertisementTracking
import com.routesme.taxi.room.factory.ViewModelFactory
import com.routesme.taxi.room.helper.DatabaseHelperImpl
import com.routesme.taxi.room.viewmodel.RoomDBViewModel
import com.routesme.taxi.App
import com.routesme.taxi.view.adapter.BottomBannerAdapter
import com.routesme.taxi.view.adapter.ImageBannerAdapter
import com.routesme.taxi.view.adapter.WifiAndQRCodeAdapter
import com.routesme.taxi.view.utils.Type
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.content_fragment.*
import kotlinx.android.synthetic.main.content_fragment.constraintLayoutDateCell
import kotlinx.android.synthetic.main.content_fragment.playerView
import kotlinx.android.synthetic.main.content_fragment.videoRingProgressBar
import kotlinx.android.synthetic.main.date_cell.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*

class ContentFragment :Fragment(),CoroutineScope by MainScope(){

    private lateinit var mContext: Context
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var device_id : String = ""
    private val SEC:Long = 300
    private var position = 0
    private val MIL:Long = 1000
    private var count = 0
    private var dialog: SpotsDialog? = null
    private var isAlive = false
    private val dateOperations = DateOperations.instance
    private lateinit var  callApiJob : Job
    private var bottomBannerAdapter : BottomBannerAdapter?=null
    private var wifiAndQRCodeAdapter : WifiAndQRCodeAdapter?=null
    private var imageBannerAdapter : ImageBannerAdapter?=null
    private lateinit var viewModel: RoomDBViewModel
    private var date = Date()
    private lateinit var glide:RequestManager
    private lateinit var imageOptions: RequestOptions
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
        sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        glide = Glide.with(App.instance)
        imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!
        callApiJob = Job()
        viewModel =  ViewModelProvider(this, ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext)))).get(RoomDBViewModel::class.java)
        WorkManager.getInstance().enqueue(App.periodicWorkRequest)
        observeTaskManager()
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

    private fun observeTaskManager(){

        WorkManager.getInstance().getWorkInfoByIdLiveData(App.periodicWorkRequest.id)
                .observe(viewLifecycleOwner, Observer { workInfo ->
                    val status = workInfo.state.name
                    if((workInfo != null) && (workInfo.state == WorkInfo.State.RUNNING)){
                        observeAnalytics()
                    }
                })
    }

    private fun observeAnalytics(){

        viewModel.getReport(DateHelper.instance.getCurrentDate()).observe(viewLifecycleOwner, Observer {

            when(it.status){

                ResponseBody.Status.SUCCESS -> {

                    it.data?.let {list->
                        val postReportViewModel: ContentViewModel by viewModels()
                        device_id.let { deviceId->
                            postReportViewModel.postReport(mContext,getJsonArray(list),deviceId).observe(viewLifecycleOwner , Observer<ReportResponse> {
                                if(it.isSuccess){

                                    observeDeleteTable()

                                }
                            })

                        }

                    }
                }
                ResponseBody.Status.ERROR -> {

                    Log.d("TaskManagerPeriodic","No Data Found")
                }
            }
        })
    }

    private fun observeDeleteTable(){

        viewModel.deleteTable(DateHelper.instance.getCurrentDate()).observe(viewLifecycleOwner, Observer {
            when(it.status){
                ResponseBody.Status.SUCCESS ->{
                    editor?.putString(SharedPreferencesHelper.from_date, DateHelper.instance.getCurrentDate().toString())
                    editor?.commit()
                }
                ResponseBody.Status.ERROR ->{

                }
            }
        })
    }

    private fun getJsonArray(list: List<AdvertisementTracking>): JsonArray {
        val jsonArray = JsonArray()
        list.forEach {
            val jsonObject = JsonObject().apply{
                addProperty("date",it.date/1000)
                addProperty("advertisementId",it.advertisementId)
                addProperty("mediaType",it.media_type)
                add("slots",getJsonArrayOfSlot(it.morning,it.noon,it.evening,it.night))
            }
            jsonArray.add(jsonObject)
        }

        return jsonArray

    }

    private fun getJsonArrayOfSlot(morning:Int,noon:Int,evening:Int,night:Int): JsonArray {
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        if(morning != 0){
            jsonObject.addProperty("period","mo")
            jsonObject.addProperty("value",morning)
        }
        if(noon != 0){
            jsonObject.addProperty("period","no")
            jsonObject.addProperty("value",noon)
        }
        if(evening != 0){
            jsonObject.addProperty("period","ev")
            jsonObject.addProperty("value",evening)
        }
        if(night != 0){
            jsonObject.addProperty("period","ni")
            jsonObject.addProperty("value",night)
        }
        jsonArray.add(jsonObject)
        return jsonArray

    }

    private  fun fetchContent(){
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
                        constraintLayoutDateCell?.let {
                            it.visibility = View.VISIBLE
                        }
                        launch {
                            setTime()
                            if (!images.isNullOrEmpty()){
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

    fun setUpWifiAndQRCodeAdapter(list:List<Data>){

        wifiAndQRCodeAdapter = WifiAndQRCodeAdapter(mContext,list)
        bottomRightPromotion.apply {
            adapter = wifiAndQRCodeAdapter
            isUserInputEnabled = false
        }

    }

    fun setUpAdapter(list:List<Data>){
        bottomBannerAdapter = BottomBannerAdapter(mContext,list)
        bottomLeftPromtion.apply {
            adapter = bottomBannerAdapter
            isUserInputEnabled = false
        }
    }

    fun setUpImageAdapter(images:List<Data>){
        imageBannerAdapter = ImageBannerAdapter(mContext,images)
        viewPageSideBanner.apply {
            adapter = imageBannerAdapter
            isUserInputEnabled = false
        }
        launch {
            while (isActive){
                images[count].contentId?.let {
                    viewModel.insertLog(it, DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(),Type.IMAGE.media_type)
                }
                viewPageSideBanner.setCurrentItem(count,true)
                bottomRightPromotion.setCurrentItem(count,true)
                if(imageBannerAdapter?.itemCount!! - 1 === count) count = 0 else count++
                delay(15 * 1000)
            }

        }

    }

    private fun removeThread(){
        if(callApiJob.isActive) callApiJob.cancelChildren()
        isAlive=false
        EventBus.getDefault().post(DemoVideo(false,""))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(animateVideo: AnimateVideo){
        try {
            launch {
                position = animateVideo.position
                bottomLeftPromtion.setCurrentItem(position, true)
            }

        }catch (e:Exception){

        }
    }

    private fun videoProgressbarRunnable() {
        launch{
            while (isActive){
                val current = (playerView.player?.currentPosition)!!.toInt()
                val progress = current * 100 / (playerView.player?.duration)!!.toInt()
                videoRingProgressBar?.progress = progress
                delay(1000)
            }
        }
    }

    private fun startThread(errorMessage:String) {
        isAlive = true
        EventBus.getDefault().post(DemoVideo(true, errorMessage))
        CoroutineScope(Dispatchers.Main + callApiJob).launch {
            delay(SEC * MIL)
            fetchContent()

        }
    }

    private fun startVideoService(list:List<Data>){
        val intent = Intent(mContext, VideoService::class.java)
        intent.putExtra("video_list", list as ArrayList<Data>)
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)

    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
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
                videoProgressbarRunnable()
            }
        }

    }
}