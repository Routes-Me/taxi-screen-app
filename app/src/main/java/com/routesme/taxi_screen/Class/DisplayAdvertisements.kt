package com.routesme.taxi_screen.Class

import android.net.Uri
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.routesme.taxi_screen.MVVM.Model.Data
import com.routesme.taxi_screen.MVVM.Model.QRCodeCallback
import com.routesme.taxi_screen.MVVM.View.HomeScreen.Fragment.ContentFragment
import com.routesme.taxiscreen.R
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import java.util.*


class DisplayAdvertisements(val qRCodeCallback: QRCodeCallback?) {

    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var simpleCache: SimpleCache? = null
    private var currentVideoIndex = 0
    private var currentBannerIndex = 0
    private lateinit var handlerBanner: Handler
    private lateinit var runnableBanner: Runnable
    //Video progress bar
    private lateinit var ringProgressBarTimer: Timer
    private lateinit var handlerProgressBar: Handler
    private lateinit var runnableProgressBar: Runnable
    private val second: Long = 1000


    init {
        //animation1.interpolator = DecelerateInterpolator()
        //animation2.interpolator = AccelerateDecelerateInterpolator()
        // videoRingProgressBar.progress = 0
        // videoRingProgressBar.max = 100
        //videoView.requestFocus()
    }

    fun displayAdvertisementVideoList(videos: List<Data>, playerView: PlayerView, ringProgressBar: RingProgressBar) {
        initPlayer(playerView)
        playVideo(videos[currentVideoIndex].url, ringProgressBar)
        simpleExoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                    }
                    Player.STATE_BUFFERING -> {
                    }
                    Player.STATE_READY -> {
                        progressBarHandlerCounter(ringProgressBar)
                        qRCodeCallback?.onVideoQRCodeChanged(videos[currentVideoIndex].qrCode)
                    }
                    Player.STATE_ENDED -> {
                        handlerProgressBar.removeCallbacks(runnableProgressBar)
                        currentVideoIndex++
                        if (currentVideoIndex < videos.size) {
                            playVideo(videos[currentVideoIndex].url, ringProgressBar)
                        } else {
                            currentVideoIndex = 0
                            playVideo(videos[currentVideoIndex].url, ringProgressBar)
                        }
                    }
                }
            }
        })
    }

    private fun initPlayer(playerView: PlayerView) {
        simpleExoPlayer = SimpleExoPlayer.Builder(App.instance).build()
        simpleCache = App.simpleCache
        cacheDataSourceFactory = CacheDataSourceFactory(simpleCache, DefaultHttpDataSourceFactory(Util.getUserAgent(App.instance, App.instance.getString(R.string.app_name))), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        playerView.player = simpleExoPlayer
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.seekTo(0, 0)
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
    }

    private fun playVideo(videoUrl: String?, ringProgressBar: RingProgressBar) {
        val videoUri = Uri.parse(videoUrl)
        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(videoUri)
        simpleExoPlayer?.prepare(mediaSource, true, false)

    }

    private fun progressBarTimerCounter(ringProgressBar: RingProgressBar) {
        ringProgressBarTimer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                ContentFragment.instance.activity?.runOnUiThread { updateRingProgressBar(ringProgressBar) }
            }
        }
        ringProgressBarTimer.schedule(task, 0, 1000)
    }

    private fun progressBarHandlerCounter(ringProgressBar: RingProgressBar) {
        runnableProgressBar = Runnable {
            updateRingProgressBar(ringProgressBar)

            handlerProgressBar.postDelayed(runnableProgressBar, second)
        }
        handlerProgressBar = Handler()
        handlerProgressBar.postDelayed(runnableProgressBar, second)
    }

    private fun updateRingProgressBar(ringProgressBar: RingProgressBar) {
        if (ringProgressBar.progress >= 100) {
            // ringProgressBarTimer.cancel()
            handlerProgressBar.removeCallbacks(runnableProgressBar)
        }
        val current = (simpleExoPlayer?.currentPosition)!!.toInt()
        val progress = current * 100 / (simpleExoPlayer?.duration)!!.toInt()
        ringProgressBar.progress = progress
    }


    fun displayAdvertisementBannerList(banners: List<Data>, ADS_ImageView: ImageView) {
        runnableBanner = Runnable {
            if (currentBannerIndex < banners.size) {
                val uri = Uri.parse(banners[currentBannerIndex].url)
                //showBannerIntoImageView(uri)
                Glide.with(App.instance).load(uri).apply(App.imageOptions).into(ADS_ImageView)
                qRCodeCallback?.onBannerQRCodeChanged(banners[currentBannerIndex].qrCode)
                currentBannerIndex++
                if (currentBannerIndex >= banners.size) {
                    currentBannerIndex = 0
                }
                handlerBanner.postDelayed(runnableBanner, 15000)
            }
        }
        handlerBanner = Handler()
        handlerBanner.postDelayed(runnableBanner, 1)
    }
}