package com.routesme.taxi.Class

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.Nullable
import androidx.core.animation.addListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.routesme.taxi.MVVM.Model.Data
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.R
import com.routesme.taxi.uplevels.App
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import org.greenrobot.eventbus.EventBus


class AdvertisementsHelper {

    //private var qrCodeCallback: QRCodeCallback? = null
    private var player: SimpleExoPlayer? = null
    private var displayImageHandler: Handler? = null
    private var displayImageRunnable: Runnable? = null
    private var progressbarHandler: Handler? = null
    private var progressbarRunnable: Runnable? = null
    private var count = 0;
    var objectAnimator:ObjectAnimator?=null
    var objectAnimator_image:ObjectAnimator?=null
    var setOut: AnimatorSet?=null
    var setIn:AnimatorSet?=null
    private var TAG="ExoPlayer Error"


    companion object {
        @get:Synchronized
        val instance = AdvertisementsHelper()
        val glide = Glide.with(App.instance)
        val imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)
        private val simpleCache = initializeVideoCaching()
        private val upstreamDataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(App.instance, App.instance.getString(R.string.app_name)))
        private val cacheDataSource = CacheDataSource.Factory().setCache(simpleCache).setUpstreamDataSourceFactory(upstreamDataSourceFactory).setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE)
        private val  mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSource)

        private fun initializeVideoCaching(): SimpleCache {
            val maxMemory = Runtime.getRuntime().maxMemory()
            val freeMemory = Runtime.getRuntime().freeMemory()
            val totalMemory = Runtime.getRuntime().totalMemory()
            val used = totalMemory -freeMemory
            val free = maxMemory - used
            Log.d("memory-size","maxMemory: $maxMemory")
            Log.d("memory-size","freeMemory: $freeMemory")
            Log.d("memory-size","totalMemory: $totalMemory")
            //val exoPlayerCacheSize: Long = 90 * 1024 * 1024
            val exoPlayerCacheSize: Long = free/5
            Log.d("memory-size","exoPlayerCacheSize: $exoPlayerCacheSize")
            val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
            val exoDatabaseProvider = ExoDatabaseProvider(App.instance)

            return SimpleCache(App.instance.cacheDir, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
        }
    }
    fun displayImages(context: Context,images: List<Data>, imageView: ImageView,imageView2: ImageView) {
        displayImageHandler = Handler()
        imageView.cameraDistance = 12000f
        imageView.pivotX = imageView.height * 0.7f
        imageView.pivotY = imageView.height / 0.7f
        var currentImageIndex = 0

        displayImageRunnable = object : Runnable {
            override fun run() {
                if (currentImageIndex < images.size) {
                    //val uri = Uri.parse(images[currentImageIndex].url)
                    EventBus.getDefault().post(images[currentImageIndex])
                    if(currentImageIndex != 0){
                        glide.load(images[currentImageIndex - 1].url).error(R.drawable.empty_promotion).placeholder(R.drawable.empty_promotion).into(imageView)
                        glide.load(images[currentImageIndex].url).error(R.drawable.empty_promotion).placeholder(R.drawable.empty_promotion).into(imageView2)
                        setImageAnimation(context,imageView,imageView2)
                    }else{

                        glide.load(images[currentImageIndex].url).error(R.drawable.empty_promotion).placeholder(R.drawable.empty_promotion).into(imageView)
                        glide.load(images[currentImageIndex].url).error(R.drawable.empty_promotion).placeholder(R.drawable.empty_promotion).into(imageView2)

                    }

                    currentImageIndex++
                    if (currentImageIndex >= images.size) {
                        currentImageIndex = 0
                    }
                }
                displayImageHandler?.postDelayed(this, 15 * 1000)
            }
        }

        displayImageHandler?.post(displayImageRunnable)
    }

    fun displayVideos(context: Context, videos: List<Data>, playerView: StyledPlayerView, progressBar: RingProgressBar,relativeLayout: RelativeLayout,relativeLayout2: RelativeLayout) {
        progressbarHandler = Handler()
        setOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_upper_out) as AnimatorSet?
        setIn = AnimatorInflater.loadAnimator(context,R.animator.card_flip_upper_in) as AnimatorSet?
        player = initPlayer(context, videos, playerView, progressBar,relativeLayout,relativeLayout2)
    }


    private fun initPlayer(context: Context, videos: List<Data>, playerView: StyledPlayerView, progressBar: RingProgressBar,relativeLayout: RelativeLayout,relativeLayout2: RelativeLayout): SimpleExoPlayer {
        val scale = context!!.getResources().getDisplayMetrics().density;
        relativeLayout.setCameraDistance(12000f)
        relativeLayout.pivotX = 0.0f
        relativeLayout.pivotY = relativeLayout.height / 0.7f
        val progressbarRunnable = videoProgressbarRunnable(progressBar)
        val defaultTrackSelector = DefaultTrackSelector(context)
        val mediaItems = videos.map { MediaItem.Builder().setUri(it.url.toString().trim()).setMediaId("${videos.indexOf(it)}").build() }
        val player = SimpleExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).setTrackSelector(defaultTrackSelector).build().apply {
            playerView.player = this
            defaultTrackSelector.setParameters(defaultTrackSelector.parameters.buildUpon().setMaxVideoBitrate(6000))
            setMediaItems(mediaItems)
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            play()
            prepare()
            addListener(object : Player.EventListener {
                override fun onMediaItemTransition(@Nullable mediaItem: MediaItem?, @Player.MediaItemTransitionReason reason: Int) {
                    val currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                    EventBus.getDefault().post(videos[currentMediaItemId])

                    setAnimation(context,relativeLayout,relativeLayout2)
                }
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            Log.d("VideoState","IDLE")
                            player?.prepare()

                        }
                        Player.STATE_BUFFERING -> {
                            Log.d("VideoState","BUFFERING")
                            count++
                            if(count >= 5 ){
                                count = 0
                                EventBus.getDefault().post(DemoVideo(true))
                            }

                        }
                        Player.STATE_READY -> {
                            Log.d("VideoState","READ")
                            count = 0
                            val currentMediaItem = playerView.player?.currentMediaItem
                            val currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                            if (currentMediaItemId == videos.indexOf(videos.first())){
                                EventBus.getDefault().post(videos[currentMediaItemId])
                                progressbarHandler?.post(progressbarRunnable)
                            }

                        }
                        Player.STATE_ENDED -> {
                            Log.d("VideoState","END")
                            progressbarHandler?.removeCallbacks(progressbarRunnable)

                        }
                    }
                }
                override fun onPlayerError(error: ExoPlaybackException) {
                    when (error.type) {
                        ExoPlaybackException.TYPE_SOURCE ->{
                            if(error.sourceException.message == "Response code: 404"){

                                player?.seekTo(player!!.getNextWindowIndex(), 0);
                                if(videos.indexOf(videos.first()) == 0) progressbarHandler?.post(progressbarRunnable)
                            }

                        }
                        ExoPlaybackException.TYPE_RENDERER -> Log.e(TAG, "TYPE_RENDERER: " + error.rendererException.message)
                        ExoPlaybackException.TYPE_UNEXPECTED -> Log.e(TAG, "TYPE_UNEXPECTED: " + error.unexpectedException.message)
                    }
                }
            })
        }
        return player
    }

    private fun setAnimation(context: Context,playerView: RelativeLayout,bgImageView: RelativeLayout){

        objectAnimator = ObjectAnimator.ofFloat(playerView, "rotationX", -180f, 0f)
        objectAnimator!!.apply {
            setDuration(1500)
            objectAnimator!!.addListener(onStart = {player!!.pause()},onEnd = {player!!.play()})
            AccelerateDecelerateInterpolator()
            start()
        }

        val zoomout: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_out)
        bgImageView.startAnimation(zoomout)
        playerView.bringToFront()
    }


    private fun setImageAnimation(context: Context,imageView: ImageView,imageView2: ImageView){

        objectAnimator_image = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 90f)
        objectAnimator_image!!.apply {
            setDuration(1500)
            AccelerateDecelerateInterpolator()
            start()
        }
        val zoomIn: Animation = AnimationUtils.loadAnimation(context, R.anim.background_zoom_in)
        imageView2.startAnimation(zoomIn)
        imageView.bringToFront()

    }

    private fun videoProgressbarRunnable(progressBar: RingProgressBar): Runnable? {
        progressbarRunnable = object : Runnable {
            override fun run() {
                val current = (player?.currentPosition)!!.toInt()
                val progress = current * 100 / (player?.duration)!!.toInt()
                progressBar.progress = progress
                progressbarHandler?.postDelayed(this, 1000)
            }
        }
        return progressbarRunnable
    }
    fun release() {
        //qrCodeCallback = null
        displayImageHandler?.removeCallbacks(displayImageRunnable)
        progressbarHandler?.removeCallbacks(progressbarRunnable)
        player?.release()
    }
}