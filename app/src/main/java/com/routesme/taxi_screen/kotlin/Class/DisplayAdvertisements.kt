package com.routesme.taxi_screen.kotlin.Class

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.net.Uri
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.routesme.taxi_screen.kotlin.Model.BannerModel
import com.routesme.taxi_screen.kotlin.Model.VideoModel
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import java.util.*

class DisplayAdvertisements(private val activity: FragmentActivity?, private val videoRingProgressBar: RingProgressBar, private val ADS_VideoView: VideoView, private val ADS_ImageView: ImageView) {

    private var currentVideoIndex = 0
    private var currentBannerIndex = 0
    private val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true)
    private val animation1 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 1f, 0f).setDuration(200)
    private val animation2 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 0f, 1f).setDuration(200)
    private var bannerRunnable: Runnable? = null
    private val proxy = activity?.let { App.getProxy(it) }
    private var videoDuration = 0
    private var ringProgressBarTimer: Timer? = null

    init {
        animation1.interpolator = DecelerateInterpolator()
        animation2.interpolator = AccelerateDecelerateInterpolator()
        videoRingProgressBar.progress = 0
        videoRingProgressBar.max = 100
        ADS_VideoView.requestFocus()
    }

    fun displayAdvertisementVideoList(videos: List<VideoModel>) {
        if (currentVideoIndex < videos.size) {
            val videoUri = Uri.parse(videos[currentVideoIndex].advertisement_URL)
            ADS_VideoView.setVideoPath(proxy?.getProxyUrl(videoUri.toString()))
            ADS_VideoView.setOnPreparedListener {
                ADS_VideoView.start()
                videoDuration = ADS_VideoView.duration
                videoRingProgressBarTimerCounter()
            }
            ADS_VideoView.setOnCompletionListener {
                currentVideoIndex++
                displayAdvertisementVideoList(videos)
            }
        } else {
            currentVideoIndex = 0
            displayAdvertisementVideoList(videos)
        }
    }
    private fun videoRingProgressBarTimerCounter() {
        ringProgressBarTimer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread { updateRingProgressBar() }
            }
        }
        ringProgressBarTimer!!.schedule(task, 0, 1000)
    }
    private fun updateRingProgressBar() {
        if (videoRingProgressBar.progress >= 100) {
            ringProgressBarTimer?.cancel()
        }
        val current = ADS_VideoView.currentPosition
        val progress = current * 100 / videoDuration
        videoRingProgressBar.progress = progress
    }
    fun displayAdvertisementBannerList(banners: List<BannerModel>) {
        bannerRunnable = Runnable {
            if (currentBannerIndex < banners.size) {
                val uri = Uri.parse(banners.get(currentBannerIndex).advertisement_URL)
                showBannerIntoImageView(uri)
                currentBannerIndex++
                if (currentBannerIndex >= banners.size) {
                    currentBannerIndex = 0
                }
                ADS_ImageView.postDelayed(bannerRunnable, 15000)
            }
        }
        ADS_ImageView.postDelayed(bannerRunnable, 1)
    }
    private fun showBannerIntoImageView(uri: Uri) {
        animation1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                activity?.let { Glide.with(it).load(uri).apply(options).into(ADS_ImageView) }
                animation2.start()
            }
        })
        animation1.start()
    }
}