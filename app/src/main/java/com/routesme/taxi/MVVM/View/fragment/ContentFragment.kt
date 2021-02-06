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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import carbon.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.*
import com.routesme.taxi.Class.*
import com.routesme.taxi.Class.SideFragmentAdapter.SideFragmentAdapter
import com.routesme.taxi.ItemAnimator
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.events.AnimateVideo
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.MVVM.service.VideoService
import com.routesme.taxi.R
import com.routesme.taxi.database.database.AdvertisementDatabase
import com.routesme.taxi.database.factory.ViewModelFactory
import com.routesme.taxi.database.helper.DatabaseHelperImpl
import com.routesme.taxi.database.viewmodel.RoomDBViewModel
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.utils.Type
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.content_fragment.*
import kotlinx.android.synthetic.main.content_fragment.view.*
import kotlinx.android.synthetic.main.side_menu_fragment.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ContentFragment :Fragment(),CoroutineScope by MainScope(),Player.EventListener {

    private lateinit var mContext: Context
    private lateinit var mView: View
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var device_id : Int = 0
    private val SEC:Long = 300
    private val MIL:Long = 1000
    private var dialog: SpotsDialog? = null
    private var isAlive = false
    private var videoShadow: RelativeLayout? = null
    private var count = 0
    private var isPlayingDemoVideo = false
    private lateinit var  callApiJob : Job
    private lateinit  var animatorVideo:ObjectAnimator
    private lateinit  var animatorImage:ObjectAnimator
    //private var player : SimpleExoPlayer?=null
    private lateinit var viewModel: RoomDBViewModel
    private lateinit var zoomOut:Animation
    private lateinit var zoomIn:Animation
    var currentMediaItemId = 0
    private var mVideoList:List<Data>?=null
    private val dateOperations = DateOperations.instance
    private lateinit var sideFragmentAdapter: SideFragmentAdapter
    private lateinit var sideFragmentCells: MutableList<ISideFragmentCell>

    private var screenWidth:Int?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.content_fragment, container, false)
        return view
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        /**
         * Called after a successful bind with our VideoService.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //We expect the service binder to be the video services binder.
            //As such we cast.
            if (service is VideoService.VideoServiceBinder) {
                Log.d("Service","Connected")
                //Then we simply set the exoplayer instance on this view.
                //Notice we are only getting information.
                playerView.player = service.getExoPlayerInstance()
                //fetchContent()

            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!.toInt()
        callApiJob = Job()
        viewModel =  ViewModelProvider(this,ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext)))).get(RoomDBViewModel::class.java)
        zoomOut = AnimationUtils.loadAnimation(context, R.anim.background_zoom_out)
        zoomIn = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
        screenWidth = DisplayManager.instance.getDisplayWidth(mContext)
        fetchContent()
        //setUpRecylerView()

    }
    private fun setUpRecylerView(){

        val date = Date()
        sideFragmentCells = mutableListOf<ISideFragmentCell>().apply {
            add(EmptyVideoDiscountCell(screenWidth!!))
            add(LargeEmptyCell())
            add(DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date)))
            add(SmallEmptyCell())
            add(WifiCell())
        }
        sideFragmentAdapter = SideFragmentAdapter(sideFragmentCells)
        recyclerView.apply {
            adapter = sideFragmentAdapter
            itemAnimator = ItemAnimator(recyclerView.context)
        }
        launch {

            setTime()

        }

    }

    @SuppressLint("SetTextI18n")
    private suspend fun setTime() {
        launch {
            while (isActive){
                val date = Date()
                sideFragmentCells[2] = DateCell(dateOperations.timeClock(date), dateOperations.dayOfWeek(date), dateOperations.date(date))
                sideFragmentAdapter.notifyDataSetChanged()
                delay(60 * 1000)
            }
        }
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
                                    pivotX = advertisementsImageView.height * 0.7f
                                    pivotY = advertisementsImageView.height / 0.7f
                                }
                                Advertisement_Video_CardView.apply {
                                    cameraDistance = 12000f
                                    pivotX = 0.0f
                                    pivotY = Advertisement_Video_CardView.height / 0.7f
                                }
                                if (!images.isNullOrEmpty())setUpImage(images)
                                launch {
                                    setImageAnimation(advertisementsImageView,advertisementsImageView2)
                                    setAnimation(Advertisement_Video_CardView,bgImage)
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

    private fun startThread(errorMessage:String){
        isAlive = true
        EventBus.getDefault().post(DemoVideo(true,errorMessage))
            CoroutineScope(Dispatchers.Main + callApiJob).launch {
                delay(SEC*MIL)
                fetchContent()

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
            changeVideoCardColor(data.tintColor)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(data: String){
        launch {
            videoProgressbarRunnable()
            playerView.player?.addListener(this@ContentFragment)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(animateVideo: AnimateVideo){

        try {
            animatorVideo.start()
            bgImage.startAnimation(zoomOut)
            Advertisement_Video_CardView.bringToFront()
        }catch (e:Exception){

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

    fun setUpImage(images: List<Data>){  //Done No more memory leakage
        var currentImageIndex = 0
        var firstTime = false
        val glide = Glide.with(mContext)
        launch{
            while(isActive) {
                if (currentImageIndex < images.size) {
                    if (currentImageIndex > 0){
                        val previousImageIndex = currentImageIndex - 1
                        val previousUri = Uri.parse(images[previousImageIndex].url)
                        glide.load(previousUri).error(R.drawable.empty_promotion).diskCacheStrategy(DiskCacheStrategy.NONE).into(advertisementsImageView)
                    }
                    val newUri = Uri.parse(images[currentImageIndex].url)
                    images[currentImageIndex].contentId?.let {
                        viewModel.insertLog(it, DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(),Type.IMAGE.media_type)
                    }
                    glide.load(newUri).error(R.drawable.empty_promotion).diskCacheStrategy(DiskCacheStrategy.NONE).into(advertisementsImageView2)
                    if (firstTime || currentImageIndex != 0){
                        firstTime = true
                        advertisementsImageView2.startAnimation(zoomIn)
                        advertisementsImageView.bringToFront()
                        animatorImage.start()
                        //setImageAnimation(advertisementsImageView,advertisementsImageView2)

                    }
                    currentImageIndex++
                    if (currentImageIndex >= images.size) {
                        currentImageIndex = 0
                    }
                    EventBus.getDefault().post(images[currentImageIndex])
                    //changeBannerQRCode(images[currentImageIndex])
                }

                delay(15 * 1000)
            }
        }
    }
    private fun changeBannerQRCode(data:Data){
        val promotion = data.promotion
        val position = 4
        if (promotion != null && promotion.isExist) sideFragmentCells.set(position,BannerDiscountCell(data)) else sideFragmentCells.set(position,WifiCell())
        sideFragmentAdapter.apply {

            //notifyItemChanged(position)
            notifyItemRemoved(position)
            notifyItemInserted(position)

        }
    }

    private fun changeVideoQRCode(data:Data){

        val promotion = data.promotion
        val position = 0
        if (promotion != null && promotion.isExist) sideFragmentCells.set(position,VideoDiscountCell(data,screenWidth!!)) else sideFragmentCells.set(position,EmptyVideoDiscountCell(screenWidth!!))
        sideFragmentAdapter.apply {

            //notifyItemChanged(position)
            notifyItemRemoved(position)
            notifyItemInserted(position)

        }

    }
    private suspend fun videoProgressbarRunnable() {
        launch{
            while (isActive){
                val current = (playerView.player?.currentPosition)!!.toInt()
                val progress = current * 100 / (playerView.player?.duration)!!.toInt()
                videoRingProgressBar?.progress = progress
                delay(1000)
            }
        }
    }
    private fun setAnimation(playerView: RelativeLayout, bgImageView: RelativeLayout){
        animatorVideo = ObjectAnimator.ofFloat(playerView, "rotationX", -180f, 0f)
        animatorVideo.apply {
            setDuration(1500)
            AccelerateDecelerateInterpolator()
        }
        /*val zoomout: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_out)
        bgImageView.startAnimation(zoomout)
        playerView.bringToFront()*/
    }

    fun setImageAnimation(imageView: ImageView, imageView2: ImageView){

        animatorImage = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 90f)
        animatorImage.apply {
            setDuration(1500)
            AccelerateDecelerateInterpolator()
            start()
        }
        /*val zoomIn: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
        imageView2.startAnimation(zoomIn)
        imageView.bringToFront()*/

    }

    fun startVideoService(list:List<Data>){
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

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        when (state) {
            Player.STATE_IDLE -> {
                Log.d("Media","STATE_IDLE")
                playerView.player?.prepare()
                playerView.player?.playbackState

            }
            Player.STATE_BUFFERING -> {
                Log.d("Media","STATE_BUFFERING")
                count++
                if(count >= 5 ){
                    count = 0
                    EventBus.getDefault().post(DemoVideo(true,"NO VIDEO CACHE"))
                    isPlayingDemoVideo = true
                }

            }
            Player.STATE_READY -> {
                Log.d("Media","STATE_READY")
                if(isPlayingDemoVideo) {
                    EventBus.getDefault().post(DemoVideo(false,""))
                    isPlayingDemoVideo = false
                }
                count = 0
                //val currentMediaItem = playerView.player?.currentMediaItem
            }
            Player.STATE_ENDED -> {

                Log.d("Media","STATE_ENDED")


            }
        }

    }
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        currentMediaItemId = playerView.player?.currentPeriodIndex!!
        Log.d("Media","onMediaItemTransition")
        //setAnimation(Advertisement_Video_CardView,bgImage)
        if(currentMediaItemId == 0) currentMediaItemId = mVideoList!!.size-1 else currentMediaItemId = currentMediaItemId-1
        currentMediaItemId.let {
            mVideoList!![it].contentId?.let {

                viewModel.insertLog(it,DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(),Type.VIDEO.media_type)

            }
        }
        animatorVideo.start()
        bgImage.startAnimation(zoomOut)
        Advertisement_Video_CardView.bringToFront()

    }
}