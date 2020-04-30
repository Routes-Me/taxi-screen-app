package com.routesme.taxi_screen.kotlin.Class

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.widget.ImageView
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.routesme.taxi_screen.kotlin.Model.BannerModel
import com.routesme.taxi_screen.kotlin.Model.VideoModel
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.ContentFragment
import com.routesme.taxiscreen.R
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import java.util.*


class DisplayAdvertisements(context: Context) {

    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var simpleCache: SimpleCache? = null

    private var currentVideoIndex = 0
    private var currentBannerIndex = 0
    private val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)
   // private val animation1 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 1f, 0f).setDuration(200)
   // private val animation2 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 0f, 1f).setDuration(200)
   private lateinit var handlerTime: Handler
    private lateinit var runnableTime: Runnable
    //private val proxy = App.getProxy()
    private var videoDuration = 0
    private lateinit var ringProgressBarTimer: Timer

    init {
        //animation1.interpolator = DecelerateInterpolator()
        //animation2.interpolator = AccelerateDecelerateInterpolator()
       // videoRingProgressBar.progress = 0
       // videoRingProgressBar.max = 100
        //videoView.requestFocus()
    }



    fun displayAdvertisementVideoList(videos: List<VideoModel>, playerView: PlayerView, ringProgressBar: RingProgressBar) {
        initPlayer(playerView)
        playVideo(videos[currentVideoIndex].advertisement_URL,ringProgressBar)
        simpleExoPlayer?.addListener(object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean,playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {}
                    Player.STATE_BUFFERING -> {}
                    Player.STATE_READY -> {
                        progressBarTimerCounter(ringProgressBar)
                    }
                    Player.STATE_ENDED -> {
                        currentVideoIndex++
                        if (currentVideoIndex < videos.size) {
                            playVideo(videos[currentVideoIndex].advertisement_URL,ringProgressBar)
                        } else {
                            currentVideoIndex = 0
                            playVideo(videos[currentVideoIndex].advertisement_URL,ringProgressBar)
                        }
                    }
                }
            }
        })
    }

    private fun initPlayer(playerView: PlayerView) {
        simpleExoPlayer = SimpleExoPlayer.Builder(App.instance).build()
        simpleCache = App.simpleCache
        cacheDataSourceFactory = CacheDataSourceFactory(simpleCache, DefaultHttpDataSourceFactory( Util.getUserAgent(App.instance, App.instance.getString(R.string.app_name)) ), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
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


    private fun updateRingProgressBar(ringProgressBar: RingProgressBar) {
        if (ringProgressBar.progress >= 100) {
            ringProgressBarTimer.cancel()
        }
        val current = (simpleExoPlayer?.currentPosition)!!.toInt()
        val progress = current * 100 / (simpleExoPlayer?.duration)!!.toInt()
        ringProgressBar.progress = progress
    }


    fun displayAdvertisementBannerList(banners: List<BannerModel>, ADS_ImageView:ImageView) {
        runnableTime = Runnable {
            if (currentBannerIndex < banners.size) {
                val uri = Uri.parse(banners.get(currentBannerIndex).advertisement_URL)
                //showBannerIntoImageView(uri)
                Glide.with(ContentFragment.instance).load(uri).apply(options).into(ADS_ImageView)
                currentBannerIndex++
                if (currentBannerIndex >= banners.size) {
                    currentBannerIndex = 0
                }
                handlerTime.postDelayed(runnableTime, 15000)
            }
        }
        handlerTime = Handler()
        handlerTime.postDelayed(runnableTime, 1)
    }
/*
    private fun showBannerIntoImageView(uri: Uri) {
        animation1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                Glide.with(it).load(uri).apply(options).into(ADS_ImageView)
                animation2.start()
            }
        })
        animation1.start()
    }
 */
}