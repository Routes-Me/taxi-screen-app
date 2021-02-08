package com.routesme.taxi.MVVM.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import com.routesme.taxi.Class.AdvertisementsHelper
import com.routesme.taxi.Class.DateHelper
import com.routesme.taxi.MVVM.Model.Data
import com.routesme.taxi.MVVM.events.AnimateVideo
import com.routesme.taxi.MVVM.events.DemoVideo
import com.routesme.taxi.MVVM.events.PromotionEvent
import com.routesme.taxi.R
import com.routesme.taxi.database.database.AdvertisementDatabase
import com.routesme.taxi.database.helper.DatabaseHelperImpl
import com.routesme.taxi.database.viewmodel.RoomDBViewModel
import com.routesme.taxi.utils.Type
import io.netopen.hotbitmapgg.library.view.RingProgressBar
import kotlinx.android.synthetic.main.content_fragment.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import kotlin.collections.ArrayList


class VideoService: Service(),CoroutineScope by MainScope(){

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var ringProgressBar:RingProgressBar
    var currentMediaItemId = 0
    private var count = 0
    private var isPlayingDemoVideo = false
    private lateinit var viewModel: RoomDBViewModel
    override fun onBind(intent: Intent?): IBinder? {

        exoPlayer.playWhenReady = true
        setMediaPlayer(intent?.getSerializableExtra("array") as List<Data>)
        return VideoServiceBinder()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /*intent?.let {
            val action = it.getIntExtra(PLAY_PAUSE_ACTION, -1)
            when (action) {
                0 -> exoPlayer.playWhenReady = false
            }
        }*/
        return super.onStartCommand(intent, flags, startId)
    }

    inner class VideoServiceBinder : Binder() {
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

    private fun setMediaPlayer(list: List<Data>) {

        exoPlayer?.apply {
            setMediaSources(getMediaSource(list))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
            play()
            volume = 0f
            addListener(object : Player.EventListener{

                override fun onMediaItemTransition(@Nullable mediaItem: MediaItem?, @Player.MediaItemTransitionReason reason: Int) {
                    currentMediaItemId = exoPlayer?.currentPeriodIndex!!
                    if(currentMediaItemId == 0) currentMediaItemId = list.size-1 else currentMediaItemId = currentMediaItemId-1
                    currentMediaItemId.let {
                        list[it].contentId?.let {

                            viewModel.insertLog(it,DateHelper.instance.getCurrentDate(), DateHelper.instance.getCurrentPeriod(),Type.VIDEO.media_type)

                        }
                    }
                    EventBus.getDefault().post(AnimateVideo(true))
                }
                override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                    super.onTracksChanged(trackGroups, trackSelections)
                    EventBus.getDefault().post(PromotionEvent(list[exoPlayer?.currentPeriodIndex!!]))
                }
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {

                            exoPlayer?.prepare()
                            exoPlayer?.playbackState

                        }
                        Player.STATE_BUFFERING -> {

                            count++
                            if(count >= 5 ){
                                count = 0
                                EventBus.getDefault().post(DemoVideo(true,"NO VIDEO CACHE"))
                                isPlayingDemoVideo = true
                            }

                        }
                        Player.STATE_READY -> {
                            if(isPlayingDemoVideo) {
                                EventBus.getDefault().post(DemoVideo(false,""))
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
                        ExoPlaybackException.TYPE_SOURCE ->{
                            if(error.sourceException.message == "Response code: 404"){
                                exoPlayer?.seekTo(exoPlayer!!.nextWindowIndex, 0)

                            }

                        }
                        ExoPlaybackException.TYPE_RENDERER ->{
                            val currentMediaItemId = currentMediaItem?.mediaId.toString().toInt()
                            if (currentMediaItemId == list.indexOf(list.first())){

                                EventBus.getDefault().post(list[currentMediaItemId])

                            }
                        }
                        ExoPlaybackException.TYPE_UNEXPECTED ->{

                        }
                    }
                }

            })
        }

        EventBus.getDefault().post("Run ProgressBar")
    }

    fun getMediaSource(videos: List<Data>): MutableList<MediaSource> {
        var mediaSource = ArrayList<MediaSource>()
            val dataSourceFactory: DataSource.Factory = CacheDataSource.Factory().setCache(AdvertisementsHelper.simpleCache).setUpstreamDataSourceFactory(DefaultHttpDataSourceFactory(Util.getUserAgent(this, getString(R.string.app_name)))).setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            videos?.let { videos ->
                for (video in videos) {

                    val mediaSourceItem = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(video.url!!))
                    mediaSource.add(mediaSourceItem)
                    Log.d("Video", "${mediaSourceItem}")
                }
                return mediaSource
            }
        }


        fun stopPlayer() {

            exoPlayer.stop()
        }

        fun playPlayer() {

            exoPlayer.play()
        }

        override fun onDestroy() {
            super.onDestroy()
            if (exoPlayer != null) {
                exoPlayer?.release()
            }
        }


}


