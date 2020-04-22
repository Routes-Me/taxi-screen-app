package com.routesme.taxi_screen.kotlin.Class

import android.net.Uri
import android.os.Handler
import android.widget.ImageView
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.routesme.taxi_screen.kotlin.Model.BannerModel
import com.routesme.taxi_screen.kotlin.Model.VideoModel
import com.routesme.taxi_screen.kotlin.View.HomeScreen.Fragment.ContentFragment
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import java.util.*


class DisplayAdvertisements() {

    private var currentVideoIndex = 0
    private var currentBannerIndex = 0
    private val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)
   // private val animation1 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 1f, 0f).setDuration(200)
   // private val animation2 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 0f, 1f).setDuration(200)
   private lateinit var handlerTime: Handler
    private lateinit var runnableTime: Runnable
    private val proxy = App.getProxy()
    private var videoDuration = 0
    private lateinit var ringProgressBarTimer: Timer

    init {
        //animation1.interpolator = DecelerateInterpolator()
        //animation2.interpolator = AccelerateDecelerateInterpolator()
       // videoRingProgressBar.progress = 0
       // videoRingProgressBar.max = 100
        //videoView.requestFocus()
    }

    companion object{
        val instance = DisplayAdvertisements()
    }

    fun displayAdvertisementVideoList(videos: List<VideoModel>,videoView:VideoView, ringProgressBar: RingProgressBar) {
        videoView.setVideoPath(getVideoProxyUrl(Uri.parse(videos[currentVideoIndex].advertisement_URL)))
        //videoView.setZOrderOnTop(true)
        videoView.setOnPreparedListener {
            videoView.start()
            videoView.requestFocus()
            //videoDuration = videoView.duration
            progressBarTimerCounter(ringProgressBar,videoView)
        }
        videoView.setOnCompletionListener {
            currentVideoIndex++
            it.reset()
            if (currentVideoIndex < videos.size) {
                videoView.setVideoPath(getVideoProxyUrl(Uri.parse(videos[currentVideoIndex].advertisement_URL)))
            } else {
                currentVideoIndex = 0
                videoView.setVideoPath(getVideoProxyUrl(Uri.parse(videos[currentVideoIndex].advertisement_URL)))
            }
        }
    }

    private fun getVideoProxyUrl(videoUrl: Uri) = proxy.getProxyUrl(videoUrl.toString())

    private fun progressBarTimerCounter(ringProgressBar: RingProgressBar, videoView: VideoView) {
        ringProgressBarTimer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                ContentFragment.instance.activity?.runOnUiThread { updateRingProgressBar(ringProgressBar,videoView) }
            }
        }
        ringProgressBarTimer.schedule(task, 0, 1000)
    }


    private fun updateRingProgressBar(ringProgressBar: RingProgressBar, videoView: VideoView) {
        if (ringProgressBar.progress >= 100) {
            ringProgressBarTimer.cancel()
        }
        val current = videoView.currentPosition
        val progress = current * 100 / videoView.duration
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