package com.routesme.vehicles.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.Data
import com.routesme.vehicles.helper.AdvertisementsHelper
import com.routesme.vehicles.helper.DateHelper
import com.routesme.vehicles.room.AdvertisementDatabase
import com.routesme.vehicles.room.helper.DatabaseHelperImpl
import com.routesme.vehicles.room.viewmodel.RoomDBViewModel
import com.routesme.vehicles.view.events.AnimateVideo
import com.routesme.vehicles.view.events.DemoVideo
import com.routesme.vehicles.view.utils.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.greenrobot.eventbus.EventBus


class VideoService : Service(), CoroutineScope by MainScope() {
    var onStartMedia = true
    private lateinit var exoPlayer: SimpleExoPlayer
    var currentMediaItemId = 0
    private var count = 0
    private var isPlayingDemoVideo = false
    private lateinit var viewModel: RoomDBViewModel
    override fun onBind(intent: Intent?): IBinder? {

        exoPlayer.playWhenReady = true
        setMediaPlayer(intent?.getSerializableExtra("video_list") as List<Data>)
        return VideoServiceBinder()

    }

    inner class VideoServiceBinder : Binder() {

        /**
         * This method should be used only for setting the exoplayer instance.
         * If exoplayer's internal are altered or accessed we can not guarantee
         * things will work correctly.
         */
        fun getExoPlayerInstance() = exoPlayer
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = SimpleExoPlayer.Builder(this).setLoadControl(getLoadControl()).build()
        viewModel = RoomDBViewModel(DatabaseHelperImpl(AdvertisementDatabase.invoke(this)))
    }

    private fun getLoadControl(): DefaultLoadControl {

        val loadControl = DefaultLoadControl.Builder()
                .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(
                        DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,  // this is it!
                        DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                )
                .setTargetBufferBytes(DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES)
                .setPrioritizeTimeOverSizeThresholds(DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS)
                .createDefaultLoadControl()

        return loadControl
    }

    fun setMediaPlayer(list: List<Data>) {

        exoPlayer.apply {
            setMediaSources(getMediaSource(list))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
            play()
            volume = 0f
            addListener(object : Player.EventListener {

                override fun onMediaItemTransition(@Nullable mediaItem: MediaItem?, @Player.MediaItemTransitionReason reason: Int) {
                    currentMediaItemId = exoPlayer.currentPeriodIndex
                    if (currentMediaItemId == 0) currentMediaItemId = list.size - 1 else currentMediaItemId = currentMediaItemId - 1
                    currentMediaItemId.let {
                        val video =  list[it]
                        video.contentId?.let {
                            viewModel.insertLog(it, video.resourceNumber!!, DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(), Type.VIDEO.media_type)
                        }
                    }
                    EventBus.getDefault().post(AnimateVideo(true, exoPlayer.currentPeriodIndex))
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {

                            exoPlayer.prepare()
                            exoPlayer.playbackState

                        }
                        Player.STATE_BUFFERING -> {

                            count++
                            if (count >= 5) {
                                count = 0
                                EventBus.getDefault().post(DemoVideo(true, "NO VIDEO CACHE"))
                                isPlayingDemoVideo = true
                            }

                        }
                        Player.STATE_READY -> {
                            if (isPlayingDemoVideo) {
                                EventBus.getDefault().post(DemoVideo(false, ""))
                                isPlayingDemoVideo = false
                            }
                            count = 0

                        }
                        Player.STATE_ENDED -> {

                        }
                    }
                }

                override fun onPlayerError(error: ExoPlaybackException) {
                    when (error.type) {
                        ExoPlaybackException.TYPE_SOURCE -> {
                            Log.e("ExoPlayer", "TYPE_SOURCE")
                            moveToNextVideo()
                            prepare()
                        }
                        ExoPlaybackException.TYPE_RENDERER -> {
                            moveToNextVideo()
                            prepare()
                            Log.e("ExoPlayer", "TYPE_RENDERER")
                        }
                        ExoPlaybackException.TYPE_UNEXPECTED -> {
                            moveToNextVideo()
                            Log.e("ExoPlayer", "TYPE_UNEXPECTED")
                        }
                    }
                }
            })
        }
    }

    private fun moveToNextVideo(){
        exoPlayer.seekTo(exoPlayer.nextWindowIndex, 0)
    }

    fun getMediaSource(videos: List<Data>): MutableList<MediaSource> {
        var mediaSource = ArrayList<MediaSource>()
        val dataSourceFactory: DataSource.Factory = CacheDataSource.Factory().setCache(AdvertisementsHelper.simpleCache).setUpstreamDataSourceFactory(DefaultHttpDataSourceFactory(Util.getUserAgent(this, getString(R.string.app_name)))).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        videos.let { videos ->
            for (video in videos) {

                val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(video.url!!))
                mediaSource.add(mediaSourceItem)
            }
            return mediaSource
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        stopSelf()
    }

}


