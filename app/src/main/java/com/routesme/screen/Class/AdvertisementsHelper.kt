package com.routesme.screen.Class

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.widget.ImageView
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.routesme.screen.MVVM.Model.Data
import com.routesme.screen.MVVM.Model.QRCodeCallback
import com.routesme.screen.uplevels.App
import com.routesme.screen.R
import io.netopen.hotbitmapgg.library.view.RingProgressBar


class AdvertisementsHelper {

    private var qrCodeCallback: QRCodeCallback? = null
    private var player: SimpleExoPlayer? = null
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
        private val upstreamDataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(App.instance, App.instance.getString(R.string.app_name)))
        private val cacheDataSource = CacheDataSource.Factory().setCache(simpleCache).setUpstreamDataSourceFactory(upstreamDataSourceFactory).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        private val  mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSource)

        private fun initializeVideoCaching(): SimpleCache {
            val maxMemory = Runtime.getRuntime().maxMemory()
            //val exoPlayerCacheSize: Long = 90 * 1024 * 1024
            val exoPlayerCacheSize: Long = maxMemory/7
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

    fun displayVideos(context: Context, videos: List<Data>, playerView: StyledPlayerView, progressBar: RingProgressBar) {
        progressbarHandler = Handler()

        player = initPlayer(context, videos, playerView, progressBar)
        player?.apply {
            prepare()
            play()
        }
    }

    private fun initPlayer(context: Context, videos: List<Data>, playerView: StyledPlayerView, progressBar: RingProgressBar): SimpleExoPlayer {
        val progressbarRunnable = videoProgressbarRunnable(progressBar)
        val mediaItems = videos.map { MediaItem.Builder().setUri(it.url).setMediaId("${videos.indexOf(it)}").build() }
        val player = SimpleExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build().apply {
            playerView.player = this
            setMediaItems(mediaItems)
            repeatMode = Player.REPEAT_MODE_ALL

            addListener(object : Player.EventListener {
                override fun onMediaItemTransition(@Nullable mediaItem: MediaItem?, @Player.MediaItemTransitionReason reason: Int) {}

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                        }
                        Player.STATE_BUFFERING -> {
                        }
                        Player.STATE_READY -> {
                            val currentMediaItem = playerView.player?.currentMediaItem
                            val currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                            progressbarHandler?.post(progressbarRunnable)
                            qrCodeCallback?.onVideoQRCodeChanged(videos[currentMediaItemId].promotion)
                        }
                        Player.STATE_ENDED -> {
                            progressbarHandler?.removeCallbacks(progressbarRunnable)
                        }
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
        qrCodeCallback = null
        displayImageHandler?.removeCallbacks(displayImageRunnable)
        progressbarHandler?.removeCallbacks(progressbarRunnable)
        player?.release()
    }
}