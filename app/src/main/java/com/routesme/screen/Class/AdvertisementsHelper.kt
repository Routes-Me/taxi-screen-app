package com.routesme.screen.Class

import android.net.Uri
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.MediaItemTransitionReason
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.routesme.screen.MVVM.Model.Data
import com.routesme.screen.MVVM.Model.QRCodeCallback
import com.routesme.screen.R
import com.routesme.screen.uplevels.App
import io.netopen.hotbitmapgg.library.view.RingProgressBar


class AdvertisementsHelper {

    private var qrCodeCallback: QRCodeCallback? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var displayImageHandler: Handler? = null
    private var displayImageRunnable: Runnable? = null
    private var progressbarHandler: Handler? = null
    private var progressbarRunnable: Runnable? = null

    companion object {
        @get:Synchronized
        val instance = AdvertisementsHelper()
        val glide = Glide.with(App.instance)
        val imageOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)
        private val simpleCache = initializeVideoCaching()
        private val cacheDataSourceFactory = CacheDataSourceFactory(simpleCache, DefaultHttpDataSourceFactory(Util.getUserAgent(App.instance, App.instance.getString(R.string.app_name))), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        val progressiveMediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
        private fun initializeVideoCaching(): SimpleCache {
            val maxMemory = Runtime.getRuntime().maxMemory()
            //val exoPlayerCacheSize: Long = 90 * 1024 * 1024
            Log.d("Memory",(maxMemory/10).toString())
            val exoPlayerCacheSize: Long = maxMemory/10
            val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
            val exoDatabaseProvider = ExoDatabaseProvider(App.instance)
            return SimpleCache(App.instance.cacheDir, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
        }
    }


    fun setQrCodeCallback(qrCodeCallback: QRCodeCallback) {
        this.qrCodeCallback = qrCodeCallback
    }

    fun displayImages(images: List<Data>, imageView: ImageView) {
        displayImageHandler = Handler()

        var currentImageIndex = 0

        displayImageRunnable = object : Runnable {
            override fun run() {
                if (currentImageIndex < images.size) {
                    val uri = Uri.parse(images[currentImageIndex].url)
                    glide.load(uri).apply(imageOptions).into(imageView)
                    qrCodeCallback?.onBannerQRCodeChanged(images[currentImageIndex].promotion)
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

    fun displayVideos(videos: List<Data>, playerView: PlayerView, progressBar: RingProgressBar) {
        progressbarHandler = Handler()
        val progressbarRunnable = videoProgressbarRunnable(progressBar)
        var currentVideoIndex = 0

        simpleExoPlayer = simpleExoPlayer().apply {
            playerView.player = this
        }

        buildMediaSource(videos[currentVideoIndex].url)?.let { simpleExoPlayer?.prepare(it, true, false) }
        simpleExoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {

                    }
                    Player.STATE_BUFFERING -> {

                    }
                    Player.STATE_READY -> {
                        progressbarHandler?.post(progressbarRunnable)
                        qrCodeCallback?.onVideoQRCodeChanged(videos[currentVideoIndex].promotion)
                    }
                    Player.STATE_ENDED -> {
                        progressbarHandler?.removeCallbacks(progressbarRunnable)
                        currentVideoIndex++
                        if (currentVideoIndex >= videos.size) {
                            currentVideoIndex = 0
                        }
                        buildMediaSource(videos[currentVideoIndex].url)?.let { simpleExoPlayer?.prepare(it) }
                    }
                }
            }
        })
    }

    private fun simpleExoPlayer(): SimpleExoPlayer? {
        return SimpleExoPlayer.Builder(App.instance).build().apply {
            playWhenReady = true
            seekTo(0, 0)
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    private fun buildMediaSource(videoUrl: String?): ProgressiveMediaSource? {
        val videoUri = Uri.parse(videoUrl)
        return progressiveMediaSource.createMediaSource(videoUri)
    }

    private fun videoProgressbarRunnable(progressBar: RingProgressBar): Runnable? {
        progressbarRunnable = object : Runnable {
            override fun run() {
                val current = (simpleExoPlayer?.currentPosition)!!.toInt()
                val progress = current * 100 / (simpleExoPlayer?.duration)!!.toInt()
                progressBar.progress = progress
                progressbarHandler?.postDelayed(this, 1000)
            }
        }
        return progressbarRunnable
    }

    fun release() {
        qrCodeCallback = null
        displayImageHandler?.removeCallbacks(displayImageRunnable)
        progressbarHandler?.removeCallbacks(progressbarRunnable)
        simpleExoPlayer?.release()
    }

}