package com.routesme.taxi.MVVM.View.fragment

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import carbon.widget.RelativeLayout
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.routesme.taxi.Class.AdvertisementsHelper
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.Class.ThemeColor
import com.routesme.taxi.MVVM.Model.*
import com.routesme.taxi.MVVM.ViewModel.ContentViewModel
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.R
import com.routesme.taxi.helper.SharedPreferencesHelper
import com.routesme.taxi.service.AdvertisementService
import com.routesme.taxi.uplevels.App
import com.routesme.taxi.utils.Type
import dmax.dialog.SpotsDialog
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import kotlinx.android.synthetic.main.content_fragment.*
import kotlinx.android.synthetic.main.content_fragment.view.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException


class ContentFragment : Fragment(),CoroutineScope by MainScope(),Player.EventListener{

    private lateinit var mContext: Context
    private lateinit var mView: View
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var device_id : Int = 0
    private val SEC:Long = 300
    private val MIL:Long = 1000
    private var dialog: SpotsDialog? = null
    private var videoRingProgressBar: RingProgressBar? = null
    private var isAlive = false
    private var videoShadow: RelativeLayout? = null
    //private lateinit var  videoProgressJob : Job
    private lateinit var  callApiJob : Job
    private lateinit var animatorVideo:ObjectAnimator
    private lateinit var animatorImage:ObjectAnimator
    private var player : SimpleExoPlayer?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.content_fragment, container, false)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mView = view
        //videoRingProgressBar = view.videoRingProgressBar
        //videoShadow = view.videoShadow
        //videoProgressJob = Job()


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = context?.getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor= sharedPreferences?.edit()
        device_id = sharedPreferences?.getString(SharedPreferencesHelper.device_id, null)!!.toInt()
        callApiJob = Job()
        //launch { setUpMediaPlayer() }
        player = SimpleExoPlayer.Builder(mContext).build()
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
                                if (!images.isNullOrEmpty())setUpImage(images)
                                launch {setUpMediaPlayer(videos)}
                                /* if (!images.isNullOrEmpty()) AdvertisementsHelper.instance.displayImages(mContext, images, mView.advertisementsImageView, mView.advertisementsImageView2, displayImageJob)*/
                                /*videoProgressJob?.let { coroutineProgressJob->
                                    AdvertisementsHelper.instance.configuringMediaPlayer(mContext, videos, mView.playerView, mView.videoRingProgressBar,mView.Advertisement_Video_CardView,mView.bgImage,coroutineProgressJob)
                                }*/
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
            CoroutineScope(Dispatchers.Main + callApiJob).launch {
                delay(SEC*MIL)
                fetchContent()

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

    fun setUpImage(images: List<Data>){
        var currentImageIndex = 0
        var firstTime = false
        CoroutineScope(Dispatchers.Main).launch {
            while(isActive) {
                if (currentImageIndex < images.size) {
                    if (currentImageIndex > 0){
                        val previousImageIndex = currentImageIndex - 1
                        val previousUri = Uri.parse(images[previousImageIndex].url)
                        AdvertisementsHelper.glide.load(previousUri).error(R.drawable.empty_promotion).into(advertisementsImageView)
                    }
                    val newUri = Uri.parse(images[currentImageIndex].url)
                    images[currentImageIndex].contentId?.toInt()?.let {

                        AdvertisementService.instance.log(it,Type.IMAGE.media_type)

                    }
                    AdvertisementsHelper.glide.load(newUri).error(R.drawable.empty_promotion).into(advertisementsImageView2)
                    if (firstTime || currentImageIndex != 0){
                        firstTime = true
                        setImageAnimation(advertisementsImageView,advertisementsImageView2)
                        EventBus.getDefault().post(images[currentImageIndex])
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

    suspend fun setUpMediaPlayer(videos: List<Data>){

        player = SimpleExoPlayer.Builder(mContext).setMediaSourceFactory(getMediaSourceFactory()).build()

    }

    suspend fun getMediaSourceFactory():DefaultMediaSourceFactory{
        return withContext(Dispatchers.Default){
            val cacheDataSourceFactory = CacheDataSource.Factory().setCache(getSimpleCache()).setUpstreamDataSourceFactory( DefaultHttpDataSourceFactory(Util.getUserAgent(App.instance, App.instance.getString(R.string.app_name)))).setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            return@withContext DefaultMediaSourceFactory(cacheDataSourceFactory)
        }

    }

    suspend fun getSimpleCache(): SimpleCache{
        return withContext(Dispatchers.Default){
            val maxMemory = Runtime.getRuntime().maxMemory()
            val freeMemory = Runtime.getRuntime().freeMemory()
            val totalMemory = Runtime.getRuntime().totalMemory()
            val used = totalMemory -freeMemory
            val free = maxMemory - used
            val exoPlayerCacheSize: Long = free/5
            return@withContext SimpleCache(App.instance.cacheDir, LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize), ExoDatabaseProvider(App.instance))
        }

    }

    fun setImageAnimation(imageView: ImageView,imageView2: ImageView){

        animatorImage = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 90f)
        animatorImage.apply {
            setDuration(1000)
            AccelerateDecelerateInterpolator()
            start()
        }
        val zoomIn: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
        imageView2.startAnimation(zoomIn)
        imageView.bringToFront()

    }
    override fun onDestroy() {
        super.onDestroy()
        AdvertisementsHelper.instance.release()
        this.cancel()
        //displayImageJob?.cancel()
        //videoProgressJob?.cancel()
        callApiJob?.cancel()

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