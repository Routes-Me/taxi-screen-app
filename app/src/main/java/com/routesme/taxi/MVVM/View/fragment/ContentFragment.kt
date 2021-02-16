package com.routesme.taxi.MVVM.View.fragment

import android.animation.ObjectAnimator
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
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import carbon.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.routesme.taxi.Class.*
import com.routesme.taxi.Class.SideFragmentAdapter.generateQrCode
import com.routesme.taxi.Class.SideFragmentAdapter.getSubtitle
import com.routesme.taxi.Class.SideFragmentAdapter.imageOptions
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.events.AnimateVideo
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.MVVM.events.PromotionEvent
import com.routesme.taxi.MVVM.service.VideoService
import com.routesme.taxi.R
import com.routesme.taxi.database.ResponseBody
import com.routesme.taxi.database.database.AdvertisementDatabase
import com.routesme.taxi.database.entity.AdvertisementTracking
import com.routesme.taxi.database.factory.ViewModelFactory
import com.routesme.taxi.database.helper.DatabaseHelperImpl
import com.routesme.taxi.database.viewmodel.RoomDBViewModel
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.utils.Type
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.banner_discount_cell.*
import kotlinx.android.synthetic.main.banner_discount_cell.bannerQrCodeImage
import kotlinx.android.synthetic.main.common_wifi_qrcode.*
import kotlinx.android.synthetic.main.content_fragment.*
import kotlinx.android.synthetic.main.content_fragment.view.*
import kotlinx.android.synthetic.main.date_cell.*
import kotlinx.android.synthetic.main.video_discount_cell.*
import kotlinx.android.synthetic.main.video_discount_cell.subTitleTv
import kotlinx.android.synthetic.main.video_discount_cell.videoQrCodeImage
import kotlinx.android.synthetic.main.video_discount_cell_two.*
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
    private var dialog: SpotsDialog? = null
    private var isAlive = false
    private var videoShadow: RelativeLayout? = null
    private val dateOperations = DateOperations.instance
    private lateinit var  callApiJob : Job
    private lateinit  var animatorVideo:ObjectAnimator
    private lateinit  var animatorImage:ObjectAnimator
    private lateinit var animatorBottomPromotion_one:ObjectAnimator
    private lateinit var animatorBottomPromotion_two:ObjectAnimator
    private lateinit var animatorBottomRight_one:ObjectAnimator
    private lateinit var animatorBottomRight_two:ObjectAnimator
    private lateinit var viewModel: RoomDBViewModel
    private lateinit var zoomOut:Animation
    private lateinit var zoomIn:Animation
    private var date = Date()
    private var mVideoList:List<Data>?=null
    private var screenWidth:Int?=null
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

    /*private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

            Log.d("Service","Service is Disconnected")

        }
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is VideoService.VideoServiceBinder) {
                playerView.player = service.getExoPlayerInstance()
                Log.d("Service","Service is Connected")
            }
        }

    }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        glide = Glide.with(App.instance)
        imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).skipMemoryCache(false)
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!
        callApiJob = Job()
        viewModel =  ViewModelProvider(this,ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext)))).get(RoomDBViewModel::class.java)
        zoomOut = AnimationUtils.loadAnimation(context, R.anim.background_zoom_out)
        zoomIn = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
        screenWidth = DisplayManager.instance.getDisplayWidth(mContext)
        WorkManager.getInstance().enqueue(App.periodicWorkRequest);
        observeTaskManager()
        setTime()
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
                    if(status == "RUNNING"){

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
                        device_id?.let {deviceId->

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
                    editor?.commit()}
                ResponseBody.Status.ERROR ->{

                }
            }
        })
    }

    private fun getJsonArray(list: List<AdvertisementTracking>): JsonArray {
        val jsonArray = JsonArray()
        list?.forEach {
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
                                advertisementsImageView.apply {
                                    cameraDistance = 12000f
                                    pivotX = height * 0.7f
                                    pivotY = height / 0.7f
                                }
                                Advertisement_Video_CardView.apply {
                                    cameraDistance = 12000f
                                    pivotX = 0.0f
                                    pivotY = height / 0.7f
                                }
                                layoutLeftBottom_two.apply {
                                    cameraDistance = 12000f
                                    pivotX = 0.0f
                                    pivotY = 0.0f
                                }
                                layoutLeftBottom_one.apply {
                                    cameraDistance = 12000f
                                    pivotX = 0.0f
                                    pivotY = 0.0f
                                }
                                layoutRightBottom_one.apply {
                                    cameraDistance = 12000f
                                    pivotX = height * 1f
                                    pivotY = height / 0.7f
                                }
                                layoutRightBottom_two.apply {
                                    cameraDistance = 12000f
                                    pivotX = height * 1f
                                    pivotY = height / 0.7f

                                }
                                launch {
                                    setBottomRightAnimation()
                                    setImageAnimation(advertisementsImageView,advertisementsImageView2)
                                    setAnimation(Advertisement_Video_CardView,bgImage)
                                    setBottomLeftAnimation()
                                    if (!images.isNullOrEmpty())setUpImage(images)
                                    mVideoList = videos
                                    startVideoService(videos)
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

    fun setUpImage(images: List<Data>){
        var currentImageIndex = 0
        var firstTime = false
        launch{
            while(isActive) {
                if (currentImageIndex < images.size) {
                    if (currentImageIndex > 0){
                        val previousImageIndex = currentImageIndex - 1
                        val previousUri = Uri.parse(images[previousImageIndex].url)
                        glide.load(previousUri).error(R.drawable.empty_promotion).apply(imageOptions).into(advertisementsImageView)
                    }
                    val newUri = Uri.parse(images[currentImageIndex].url)
                    images[currentImageIndex].contentId?.let {
                        viewModel.insertLog(it, DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(),Type.IMAGE.media_type)
                    }

                    glide.load(newUri).error(R.drawable.empty_promotion).apply(imageOptions).into(advertisementsImageView2)
                    if (firstTime || currentImageIndex != 0){
                        firstTime = true
                        launch {
                            advertisementsImageView2.startAnimation(zoomIn)
                            advertisementsImageView.bringToFront()
                            animatorImage.start()
                        }
                    }
                    currentImageIndex++
                    if (currentImageIndex >= images.size) {
                        currentImageIndex = 0
                    }
                    if(images[currentImageIndex].promotion !=null) {
                        animatorBottomRight_one.start()
                        changeBannerQRCode(images[currentImageIndex])

                    }else{

                        animatorBottomRight_two.start()
                    }


                }
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
    fun onEvent(data: Data){
        if (data.type ==  ContentType.Video.value){
            launch {
                changeVideoCardColor(data.tintColor)
            }
        }
    }

   /* @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(data: String){
        launch {
            videoProgressbarRunnable()
        }
  }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(animateVideo: AnimateVideo){
        try {
            launch {

                animatorVideo.start()
                bgImage.startAnimation(zoomOut)
                Advertisement_Video_CardView.bringToFront()
                position = animateVideo.position
                mVideoList?.let {list->
                    if(list[position].promotion !=null){
                        animatorBottomPromotion_two.start()
                        changeVideoQRCode(list[animateVideo.position])
                    }else{

                        animatorBottomPromotion_one.start()

                    }
                }
            }

        }catch (e:Exception){

        }
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(promotionEvent: PromotionEvent){
        try {
            launch {
                Log.d("Promotion","Promotion Animation")
                position = promotionEvent.position
                mVideoList?.let {list->
                    if(list[position].promotion !=null){
                        animatorBottomPromotion_two.start()
                       // changeVideoQRCode(list[promotionEvent.position])
                    }else{

                        animatorBottomPromotion_one.start()

                    }
                }
            }

        }catch (e:Exception){ }
    }*/

    fun setLayout(data: Data){
        val promotion = data.promotion
        val tintColor = data.tintColor
        promotion?.let {
            val link = it.link
            if (!link.isNullOrEmpty()) {
                Log.d("Promotion","I Called ${data}")
                val color = ThemeColor(tintColor).getColor()
                videoPromotionCard_two.setElevationShadowColor(color)
                promotion.logoUrl?.let { logoUrl ->

                    glide.load(logoUrl).apply(imageOptions).into(videoLogoImage_two)
                    videoLogoImage_two.visibility = View.VISIBLE
                }
                if (!promotion.title.isNullOrEmpty()) titleTv_two.text = promotion.title
                subTitleTv_two.text = getSubtitle(promotion.subtitle, promotion.code, color)
                generateQrCode(link, color).let { qrCode ->
                    glide.load(qrCode).apply(imageOptions).into(videoQrCodeImage_two)
                }
            }
        }
    }
    private suspend fun changeVideoCardColor(tintColor: Int?) {
        val color = ThemeColor(tintColor).getColor()
        val lowOpacityColor = ColorUtils.setAlphaComponent(color,33)
        videoShadow?.setElevationShadowColor(color)
        videoRingProgressBar?.let {
            it.ringColor = lowOpacityColor
            it.ringProgressColor = color
        }
    }

    private fun changeBannerQRCode(data: Data) {
        val promotion = data.promotion
        if (promotion != null && promotion.isExist) {
            val promotion = data.promotion
            val tintColor = data.tintColor
            promotion?.let {
                it.link?.let {link ->
                    val color = ThemeColor(tintColor).getColor()
                    generateQrCode(link,color).let {qrCode ->
                        glide.load(qrCode).apply(imageOptions).into(bannerQrCodeImage)
                        glide.load(qrCode).apply(imageOptions).into(bannerQrCodeImage_two)

                    }
                }
            }

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

    private fun setAnimation(playerView_layout: RelativeLayout, bgImageView: RelativeLayout){
        animatorVideo = ObjectAnimator.ofFloat(playerView_layout, "rotationX", -180f, 0f)
        animatorVideo.apply {
            setDuration(2000)
            addListener(onStart = {
                //playerView.player?.play()
            },onEnd = {
               // playerView.player?.stop()
            })
            AccelerateDecelerateInterpolator()
        }
    }

    private fun setBottomLeftAnimation(){
        animatorBottomPromotion_two = ObjectAnimator.ofFloat(layoutLeftBottom_two,"rotationX",180f,0f)
        animatorBottomPromotion_two.apply {
            duration = 1500
            addListener(onStart = {
                if(layoutLeftBottom_two.visibility == View.INVISIBLE) {
                    layoutLeftBottom_two.visibility = View.VISIBLE
                    layoutLeftBottom_two.bringToFront()
                }

            },onEnd = {

                if(layoutLeftBottom_one.visibility == View.VISIBLE ) layoutLeftBottom_one.visibility = View.INVISIBLE
                if(emptyCardView_two.visibility == View.VISIBLE) emptyCardView_two.visibility = View.INVISIBLE
                if(videoPromotionCard_two.visibility == View.INVISIBLE) videoPromotionCard_two.visibility = View.VISIBLE
                if(mVideoList!![position].promotion!=null) setLayout(mVideoList!![position])

            })
            AccelerateInterpolator()
        }

        animatorBottomPromotion_one = ObjectAnimator.ofFloat(layoutLeftBottom_one,"rotationX",180f, 0f)
        animatorBottomPromotion_one?.apply {
            duration = 1500
            addListener(onStart = {
                if(layoutLeftBottom_one.visibility == View.INVISIBLE){
                    layoutLeftBottom_one.visibility = View.VISIBLE
                    layoutLeftBottom_one.bringToFront()
                }
            },onEnd = {
                if(layoutLeftBottom_two.visibility == View.VISIBLE) layoutLeftBottom_two.visibility = View.INVISIBLE
                if(videoPromotionCard_two.visibility == View.VISIBLE) videoPromotionCard_two.visibility = View.INVISIBLE
                if(emptyCardView_two.visibility == View.INVISIBLE) emptyCardView_two.visibility = View.VISIBLE

            })
            AccelerateInterpolator()
        }
    }


    private fun setBottomRightAnimation(){
        animatorBottomRight_one = ObjectAnimator.ofFloat(layoutRightBottom_one,"rotationY", 0f, 90f)
        animatorBottomRight_one.apply {
            duration = 1500
            addListener(onStart = {
                if(layoutRightBottom_one.visibility == View.INVISIBLE)layoutRightBottom_one.visibility = View.VISIBLE
                layoutRightBottom_one.bringToFront()
                qrCode_cell_two.startAnimation(zoomIn)
            },onEnd = {
                if(qrCode_cell_two.visibility == View.INVISIBLE) qrCode_cell_two.visibility = View.VISIBLE
                if(wifi_cell_two.visibility == View.VISIBLE) wifi_cell_two.visibility = View.INVISIBLE
                if(layoutRightBottom_two.visibility == View.VISIBLE) layoutRightBottom_two.visibility = View.INVISIBLE

            })
            AccelerateInterpolator()
        }

        animatorBottomRight_two = ObjectAnimator.ofFloat(layoutRightBottom_two,"rotationY", 0f, 90f)
        animatorBottomRight_two?.apply {
            duration = 1500
            addListener(onStart = {
                if(layoutRightBottom_two.visibility == View.INVISIBLE)layoutRightBottom_two.visibility = View.VISIBLE
                layoutRightBottom_two.bringToFront()
                wifi_cell_two.startAnimation(zoomIn)
            },onEnd = {
                if(layoutLeftBottom_one.visibility == View.VISIBLE) layoutLeftBottom_one.visibility = View.INVISIBLE
                if(qrCode_cell_two.visibility == View.VISIBLE) qrCode_cell_two.visibility = View.INVISIBLE
                if(wifi_cell_two.visibility == View.INVISIBLE) wifi_cell_two.visibility = View.VISIBLE


            })
            AccelerateInterpolator()
        }
    }

    private fun setImageAnimation(imageView: ImageView, imageView2: ImageView){

        animatorImage = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 90f)
        animatorImage.apply {
            setDuration(1500)
            AccelerateDecelerateInterpolator()

        }

    }

    private fun changeVideoQRCode(data: Data) {
        val promotion = data.promotion
        val tintColor = data.tintColor
            promotion?.let {
                val link = it.link
                if (!link.isNullOrEmpty()) {
                    val color = ThemeColor(tintColor).getColor()
                    videoPromotionCard.setElevationShadowColor(color)
                    promotion.logoUrl?.let { logoUrl ->
                        glide.load(logoUrl).apply(imageOptions).into(videoLogoImage)
                        videoLogoImage.visibility = View.VISIBLE
                    }
                    if (!promotion.title.isNullOrEmpty()) titleTv.text = promotion.title
                    subTitleTv.text = getSubtitle(promotion.subtitle, promotion.code, color)
                    generateQrCode(link, color).let { qrCode ->
                        glide.load(qrCode).apply(imageOptions).into(videoQrCodeImage)
                    }
                }
            }
    }

    private fun startThread(errorMessage:String){
        isAlive = true
        EventBus.getDefault().post(DemoVideo(true,errorMessage))
        CoroutineScope(Dispatchers.Main + callApiJob).launch {
            delay(SEC*MIL)
            fetchContent()

        }
    }


    private fun startVideoService(list:List<Data>){
        val intent = Intent(mContext, VideoService::class.java)
        intent.putExtra("array", list as ArrayList<Data>)
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

            Log.d("Service","Service is Disconnected")

        }
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is VideoService.VideoServiceBinder) {
                playerView.player = service.getExoPlayerInstance()
                videoProgressbarRunnable()
                Log.d("Service","Service is Connected")
            }
        }

    }

}