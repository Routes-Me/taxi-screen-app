package com.routesme.taxi.Class

import android.animation.*
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.annotation.Nullable
import carbon.widget.RelativeLayout
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
    fun displayImages(context: Context, images: List<Data>, imageView: ImageView) {

        var videoUrl: Uri? = null
        var oa1: ObjectAnimator? = null

        val distance = 8000
        val scale: Float = context.resources.displayMetrics.density * distance
        imageView.apply {
            cameraDistance = scale
            pivotX = this.width.toFloat()
            oa1 = ObjectAnimator.ofFloat(this, "rotationY", 0F, 180f).setDuration(2000)
            val oa2: ObjectAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0f, 1f)
            oa1?.interpolator = DecelerateInterpolator()
            oa2.interpolator = AccelerateDecelerateInterpolator()
            oa1?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    // imageView.setImageResource(R.drawable.frontSide)
                    videoUrl?.let { glide.load(it).apply(imageOptions).into(imageView) }
                    oa2.start()
                }
            })
        }



/*
        val bannerAnimation = AnimatorInflater.loadAnimator(context, R.animator.banner_animation) as AnimatorSet
        val distance = 8000
        val scale: Float = context.resources.displayMetrics.density * distance

        imageView.apply {
            cameraDistance = scale
            pivotX = this.width.toFloat()
            bannerAnimation.setTarget(this)
        }
*/
        displayImageHandler = Handler()

        var currentImageIndex = 0

        displayImageRunnable = object : Runnable {
            override fun run() {
                if (currentImageIndex < images.size) {
                   // bannerAnimation.start()
                    // qrCodeCallback?.onBannerQRCodeChanged(images[currentImageIndex])
                    EventBus.getDefault().post(images[currentImageIndex])
                    //videoUrl = Uri.parse(images[currentImageIndex].url)
                    //oa1?.start()

                    val url = Uri.parse(images[currentImageIndex].url)
                    url?.let { glide.load(it).apply(imageOptions).into(imageView) }

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

    fun displayVideos(context: Context, videos: List<Data>, playerView: StyledPlayerView, videoCardView: RelativeLayout, progressBar: RingProgressBar) {
        progressbarHandler = Handler()
        player = initPlayer(context, videos, playerView, videoCardView, progressBar)
    }

    private fun initPlayer(context: Context, videos: List<Data>, playerView: StyledPlayerView, videoCardView: RelativeLayout, progressBar: RingProgressBar): SimpleExoPlayer {
        //val rotate_animation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation)
        val videoAnimation = AnimatorInflater.loadAnimator(context, R.animator.video_animation) as AnimatorSet
        //val videoAnimationOut = AnimatorInflater.loadAnimator(context, R.animator.out_animation) as AnimatorSet
        val distance = 8000
        val scale: Float = context.resources.displayMetrics.density * distance
        videoCardView.apply {
            cameraDistance = scale
            pivotY = this.height.toFloat()
            //videoAnimationOut.setTarget(this)
            videoAnimation.setTarget(this)

        }

        val progressbarRunnable = videoProgressbarRunnable(progressBar)
        val defaultTrackSelector = DefaultTrackSelector(context)
        //Log.d()
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

                    //videoCardView.startAnimation(rotate_animation)

                  //  videoAnimation.start()

                    val currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                   // qrCodeCallback?.onVideoQRCodeChanged(videos[currentMediaItemId].promotion)
                    EventBus.getDefault().post(videos[currentMediaItemId])
                }
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            player?.prepare()

                        }
                        Player.STATE_BUFFERING -> {
                            count++
                            if(count >= 5 ){
                                count = 0
                                EventBus.getDefault().post(DemoVideo(true))
                            }

                        }
                        Player.STATE_READY -> {
                            /*
                            playerView.animate()
                                    .rotationX(360F)
                                    .setDuration(7000)
                                    .setStartDelay(500)
                                    .interpolator = AccelerateDecelerateInterpolator()
*/
                         //   playerView.animation.start()

                            count = 0
                            val currentMediaItem = playerView.player?.currentMediaItem
                            val currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                            if (currentMediaItemId == videos.indexOf(videos.first())){
                                EventBus.getDefault().post(videos[currentMediaItemId])
                                progressbarHandler?.post(progressbarRunnable)
                            }
                        }
                        Player.STATE_ENDED -> {
                            //Log.d("vidoePlayer-state","STATE_ENDED")
                            progressbarHandler?.removeCallbacks(progressbarRunnable)
                           // videoAnimationOut.start()
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