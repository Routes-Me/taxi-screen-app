package com.routesme.taxi.view.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.text.SpannedString
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
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkInfo
import androidx.work.WorkManager
import carbon.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.routesme.taxi.data.model.ContentResponse
import com.routesme.taxi.data.model.ContentType
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
import com.routesme.taxi.view.utils.Type
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.banner_discount_cell.*
import kotlinx.android.synthetic.main.common_wifi_qrcode.*
import kotlinx.android.synthetic.main.content_fragment.*
import kotlinx.android.synthetic.main.date_cell.*
import kotlinx.android.synthetic.main.video_discount_cell.*
import kotlinx.android.synthetic.main.video_discount_cell_two.*
import kotlinx.coroutines.*
import net.codecision.glidebarcode.model.Barcode
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
    private var animatorVideo:ObjectAnimator?=null
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
    private lateinit var glide:RequestManager
    private lateinit var imageOptions: RequestOptions
    private var contentViewModel: ContentViewModel? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentViewModel = activity?.let { ViewModelProvider(it).get(ContentViewModel::class.java) }
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
        zoomOut = AnimationUtils.loadAnimation(context, R.anim.background_zoom_out)
        zoomIn = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
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
                    Log.d("WORKER","${status}")
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
                contentViewModel?.getContent(1,100,mContext)?.observe(viewLifecycleOwner , Observer<ContentResponse> {
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
                                constraintLayoutDateCell?.let {
                                    it.visibility = View.VISIBLE
                                }
                                launch {
                                    setBottomRightAnimation()
                                    setImageAnimation(advertisementsImageView,advertisementsImageView2)
                                    setAnimation(Advertisement_Video_CardView,bgImage)
                                    setBottomLeftAnimation()
                                    setTime()
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

    private fun setUpImage(images: List<Data>){
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(animateVideo: AnimateVideo){
        try {
            launch {
                animatorVideo?.start()
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

    private fun setLayout(data: Data){
        val promotion = data.promotion
        val tintColor = data.tintColor
        promotion?.let {
            val link = it.link
            if (!link.isNullOrEmpty()) {
                val color = ThemeColor(tintColor).getColor()
                videoPromotionCard_two.setElevationShadowColor(color)
                if(promotion.logoUrl !=null){
                    glide.load(promotion.logoUrl).apply(imageOptions).into(videoLogoImage_two)
                    videoLogoImage_two.visibility = View.VISIBLE
                }else videoLogoImage_two.visibility = View.GONE
                if (!promotion.title.isNullOrEmpty()) titleTv_two.text = promotion.title
                subTitleTv_two.text = getSubtitle(promotion.subtitle, promotion.code, color)
                generateQrCode(link, color).let {
                    glide.load(it).apply(imageOptions).into(videoQrCodeImage_two)
                }

            }
        }
    }
    private  fun changeVideoCardColor(tintColor: Int?) {
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
            promotion.let {
                it.link?.let {link ->
                    val color = ThemeColor(tintColor).getColor()
                    generateQrCode(link,color).let {
                        glide.load(it).apply(imageOptions).into(bannerQrCodeImage)
                        glide.load(it).apply(imageOptions).into(bannerQrCodeImage_two)

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
        animatorVideo?.apply {
            duration = 2000
            AccelerateDecelerateInterpolator()
        }
    }

    private fun getSubtitle(subtitle: String?, code: String?, color: Int): SpannedString {
        return buildSpannedString {
            if (!subtitle.isNullOrBlank()){
                append(subtitle)
            }
            if (!code.isNullOrEmpty()){
                if (!subtitle.isNullOrEmpty()) append(", ")
                bold { color(color) { append("Use code ") } }
                append(code)
            }
        }
    }

    private fun generateQrCode(promotionLink: String, color: Int): Barcode {
        return Barcode(promotionLink, BarcodeFormat.QR_CODE,color, Color.TRANSPARENT)
    }

    private fun setBottomLeftAnimation(){
        animatorBottomPromotion_two = ObjectAnimator.ofFloat(layoutLeftBottom_two,"rotationX",180f,0f)
        animatorBottomPromotion_two.apply {
            duration = 1500
            addListener(onStart = {
                layoutLeftBottom_two?.let {
                    it.visibility = View.VISIBLE
                    it.bringToFront()
                }

            },onEnd = {
                layoutLeftBottom_one?.let {
                    it.visibility = View.INVISIBLE
                }
                emptyCardView_two?.let {
                    it.visibility = View.INVISIBLE
                }
                videoPromotionCard_two?.let {
                    it.visibility = View.VISIBLE
                }
                launch {
                    delay(3000)
                    if(mVideoList!![position].promotion!=null) setLayout(mVideoList!![position])
                }
            })
            AccelerateInterpolator()
        }

        animatorBottomPromotion_one = ObjectAnimator.ofFloat(layoutLeftBottom_one,"rotationX",180f, 0f)
        animatorBottomPromotion_one.apply {
            duration = 1500
            addListener(onStart = {
                layoutLeftBottom_one?.let {
                    it.visibility = View.VISIBLE
                    it.bringToFront()
                }
            },onEnd = {
                layoutLeftBottom_two?.let {
                    it.visibility = View.INVISIBLE
                }
                videoPromotionCard_two?.let {
                    it.visibility = View.INVISIBLE
                }
                emptyCardView_two?.let {
                    it.visibility = View.VISIBLE
                }
            })
            AccelerateInterpolator()
        }
    }

    private fun setBottomRightAnimation(){
        animatorBottomRight_one = ObjectAnimator.ofFloat(layoutRightBottom_one,"rotationY", 0f, 90f)
        animatorBottomRight_one.apply {
            duration = 1500
            addListener(onStart = {
                layoutRightBottom_one.let {
                    it.visibility = View.VISIBLE
                    it.bringToFront()
                }
            },onEnd = {
                layoutRightBottom_two?.let {
                    it.visibility = View.INVISIBLE
                }
                qrCode_cell_two?.let {
                    it.visibility = View.VISIBLE
                }
                wifi_cell_two?.let {
                    it.visibility = View.INVISIBLE
                }
            })
            AccelerateInterpolator()
        }

        animatorBottomRight_two = ObjectAnimator.ofFloat(layoutRightBottom_two,"rotationY", 0f, 90f)
        animatorBottomRight_two.apply {
            duration = 1500
            addListener(onStart = {
                layoutRightBottom_two.let {
                    it.visibility = View.VISIBLE
                    it.bringToFront()
                }
            },onEnd = {
                layoutRightBottom_two?.let {
                    it.visibility = View.INVISIBLE
                }
                qrCode_cell_two?.let {
                    it.visibility = View.INVISIBLE
                }
                wifi_cell_two?.let {
                    it.visibility = View.VISIBLE
                }
            })
            AccelerateInterpolator()
        }
    }

    private fun setImageAnimation(imageView: ImageView, imageView2: ImageView){
        animatorImage = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 90f)
        animatorImage.apply {
            duration = 1500
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
                    if(promotion.logoUrl !=null){
                        glide.load(promotion.logoUrl).apply(imageOptions).into(videoLogoImage)
                        videoLogoImage.visibility = View.VISIBLE
                    }else videoLogoImage.visibility = View.GONE

                    if (!promotion.title.isNullOrEmpty()) titleTv.text = promotion.title
                    subTitleTv.text = getSubtitle(promotion.subtitle, promotion.code, color)
                    generateQrCode(link, color).let { qrCode ->
                        glide.load(qrCode).apply(imageOptions).into(videoQrCodeImage)
                    }
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