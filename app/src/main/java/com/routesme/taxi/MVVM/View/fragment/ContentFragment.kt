package com.routesme.taxi.MVVM.View.fragment

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.Nullable
import androidx.core.animation.addListener
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import carbon.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import com.routesme.taxi.Class.AdvertisementsHelper
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.ContentResponse
import com.routesme.taxi.MVVM.Model.ContentType
import com.routesme.taxi.MVVM.Model.Data
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.events.DemoVideo
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
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

class ContentFragment :Fragment(),CoroutineScope by MainScope(){

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
    private lateinit var zoomIn:Animation
    private lateinit var zoomOut:Animation
    private var player : SimpleExoPlayer?=null
    private lateinit var viewModel: RoomDBViewModel

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
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!.toInt()
        callApiJob = Job()
        viewModel =  ViewModelProviders.of(this,ViewModelFactory(DatabaseHelperImpl(AdvertisementDatabase.invoke(mContext)))).get(RoomDBViewModel::class.java)
        player = SimpleExoPlayer.Builder(mContext).setMediaSourceFactory(getMediaSourceFactory()).setTrackSelector(DefaultTrackSelector(mContext)).setLoadControl(getExoPlayerCustomeControl()).build()
        zoomIn = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
        zoomOut = AnimationUtils.loadAnimation(context, R.anim.background_zoom_out)
        fetchContent()
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
                                //setImageAnimation(advertisementsImageView,advertisementsImageView2)
                                if (!images.isNullOrEmpty())setUpImage(images)
                                launch {

                                    setUpMediaPlayer(videos)
                                    videoProgressbarRunnable()
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
        animatorImage = ObjectAnimator.ofFloat(advertisementsImageView, "rotationY", 0f, 90f)
        animatorImage?.apply {
            duration = 1000
            AccelerateDecelerateInterpolator()
        }
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
                        animatorImage?.start()
                        //setImageAnimation(advertisementsImageView,advertisementsImageView2)
                        EventBus.getDefault().post(images[currentImageIndex])
                        advertisementsImageView2.startAnimation(zoomIn)
                        advertisementsImageView.bringToFront()
                    }
                    currentImageIndex++
                    if (currentImageIndex >= images.size) {
                        currentImageIndex = 0
                    }
                }

                delay(15 * 1000)
            }
        }

    }

    private suspend fun setUpMediaPlayer(videos: List<Data>){
        var currentMediaItemId = 0
        val mediaItems = videos.map { MediaItem.Builder().setUri(it.url.toString().trim()).setMediaId("${videos.indexOf(it)}").build() }
        animatorVideo = ObjectAnimator.ofFloat(Advertisement_Video_CardView, "rotationX", -180f, 0f)
        animatorVideo?.apply {
            duration = 1000
            animatorVideo?.addListener(onStart = {player?.pause()},onEnd = {player?.play()})
            AccelerateDecelerateInterpolator()
        }
        player?.apply {
            playerView.player = this
            setMediaItems(mediaItems)
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
            play()
            volume = 0f
            addListener(object : Player.EventListener {
                override fun onMediaItemTransition(@Nullable mediaItem: MediaItem?, @Player.MediaItemTransitionReason reason: Int) {
                    currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                    EventBus.getDefault().post(videos[currentMediaItemId])
                    bgImage.startAnimation(zoomOut)
                    Advertisement_Video_CardView.bringToFront()
                    animatorVideo.start()
                    if(currentMediaItemId == 0) currentMediaItemId = videos.size-1 else currentMediaItemId = currentMediaItemId-1
                    currentMediaItemId.let {
                        videos[it].contentId?.let {

                            viewModel.insertLog(it,DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(),Type.VIDEO.media_type)

                        }
                    }
                }
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {

                            player?.prepare()
                            player?.playbackState

                        }
                        Player.STATE_BUFFERING -> {

                            count++
                            if(count >= 5 ){
                                count = 0
                                EventBus.getDefault().post(DemoVideo(true,"NO VIDEO CACHE"))
                                isPlayingDemoVideo = true
                            }

                        }
                        Player.STATE_READY -> {
                            if(isPlayingDemoVideo) {
                                EventBus.getDefault().post(DemoVideo(false,""))
                                isPlayingDemoVideo = false
                            }
                            count = 0
                            val currentMediaItem = playerView.player?.currentMediaItem
                            val currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                            if (currentMediaItemId == videos.indexOf(videos.first())){

                                EventBus.getDefault().post(videos[currentMediaItemId])

                            }
                        }
                        Player.STATE_ENDED -> {

                        }
                    }
                }
                override fun onPlayerError(error: ExoPlaybackException) {
                    when (error.type) {
                        ExoPlaybackException.TYPE_SOURCE ->{
                            if(error.sourceException.message == "Response code: 404"){
                                player?.seekTo(player!!.nextWindowIndex, 0)

                            }

                        }
                        ExoPlaybackException.TYPE_RENDERER ->{


                        }
                        ExoPlaybackException.TYPE_UNEXPECTED ->{

                        }
                    }
                }
            })
        }
    }

    fun getExoPlayerCustomeControl():LoadControl{
        val loadControl = DefaultLoadControl.Builder()
                .setAllocator(DefaultAllocator(true, 16))
                .setBufferDurationsMs(2000,
                        5000,
                        1500,
                        2000)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl()

        return loadControl
    }

    private suspend fun videoProgressbarRunnable() {
        launch(newSingleThreadContext("ProgressBar")) {
            while (isActive){
                Log.d("Thread","ProgressBar")
                val current = (player?.currentPosition)!!.toInt()
                val progress = current * 100 / (player?.duration)!!.toInt()
                videoRingProgressBar?.progress = progress
                delay(1000)
            }
        }
    }

    /*private suspend fun getMediaSourceFactory():DefaultMediaSourceFactory{
        return withContext(Dispatchers.Default){
            val cacheDataSourceFactory = CacheDataSource.Factory().setCache(AdvertisementsHelper.simpleCache).setUpstreamDataSourceFactory(DefaultHttpDataSourceFactory(Util.getUserAgent(mContext,getString(R.string.app_name)))).setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            return@withContext DefaultMediaSourceFactory(cacheDataSourceFactory)
        }

    }*/

    private fun getMediaSourceFactory():DefaultMediaSourceFactory{
        val cacheDataSourceFactory = CacheDataSource.Factory().setCache(AdvertisementsHelper.simpleCache).setUpstreamDataSourceFactory(DefaultHttpDataSourceFactory(Util.getUserAgent(mContext,getString(R.string.app_name)))).setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        return   DefaultMediaSourceFactory(cacheDataSourceFactory)
    }

    /*fun setImageAnimation(imageView: ImageView,imageView2: ImageView){

        val zoomIn: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
        imageView2.startAnimation(zoomIn)
        imageView.bringToFront()

    }*/

    /*private fun setAnimation(playerView: RelativeLayout,bgImageView: RelativeLayout){
        val animatorVideo = ObjectAnimator.ofFloat(playerView, "rotationX", -180f, 0f)
        animatorVideo?.apply {
            duration = 1000
            animatorVideo?.addListener(onStart = {player?.pause()},onEnd = {player?.play()})
            AccelerateDecelerateInterpolator()
            start()
        }
        val zoomout: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_out)
        bgImageView.startAnimation(zoomout)
        playerView.bringToFront()
    }*/

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        player?.release()
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


}