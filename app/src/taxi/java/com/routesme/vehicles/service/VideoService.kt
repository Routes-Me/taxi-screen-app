package com.routesme.vehicles.service

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.routesme.vehicles.R
import com.routesme.vehicles.data.model.Data
import com.routesme.vehicles.helper.AdvertisementsHelper
import com.routesme.vehicles.helper.DateHelper
import com.routesme.vehicles.helper.SharedPreferencesHelper
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
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isCacheCleared:Boolean?=null
    private lateinit var viewModel: RoomDBViewModel
    private var advertisementHelper = AdvertisementsHelper()
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
        sharedPreferences = getSharedPreferences(SharedPreferencesHelper.device_data, Activity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
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
                            //Toast.makeText(this@VideoService,"STATE_IDLE",Toast.LENGTH_LONG).show()
                        }
                        Player.STATE_BUFFERING -> {
                            count++
                            if (count >= 5) {
                                count = 0
                                EventBus.getDefault().post(DemoVideo(true, "NO VIDEO CACHE"))
                                editor.putBoolean(SharedPreferencesHelper.isCacheClear, false).apply()
                                isPlayingDemoVideo = true
                            }
                           // Toast.makeText(this@VideoService,"STATE_BUFFERING",Toast.LENGTH_LONG).show()
                        }
                        Player.STATE_READY -> {
                            if (isPlayingDemoVideo) {
                                EventBus.getDefault().post(DemoVideo(false, ""))
                                isPlayingDemoVideo = false
                            }
                            count = 0
                        }
                        Player.STATE_ENDED -> {
                            //Toast.makeText(this@VideoService,"State End",Toast.LENGTH_LONG).show()
                            exoPlayer.prepare()
                            exoPlayer.playbackState
                        }
                    }
                }

                override fun onPlayerError(error: ExoPlaybackException) {
                    when (error.type) {
                        ExoPlaybackException.TYPE_SOURCE -> {
                            Log.e("ExoPlayer", "TYPE_SOURCE ${error.sourceException}")
                            //Toast.makeText(this@VideoService,"TYPE_SOURCE ${error.sourceException}",Toast.LENGTH_LONG).show()
                                moveToNextVideo()
                                prepare()
                        }
                        ExoPlaybackException.TYPE_RENDERER -> {
                            //Toast.makeText(this@VideoService,"TYPE_RENDERER ",Toast.LENGTH_LONG).show()
                            moveToNextVideo()
                            prepare()
                            Log.e("ExoPlayer", "TYPE_RENDERER")
                        }
                        ExoPlaybackException.TYPE_UNEXPECTED -> {
                            advertisementHelper.deleteCache()
                            moveToNextVideo()
                            //Toast.makeText(this@VideoService,"TYPE_UNEXPECTED ",Toast.LENGTH_LONG).show()
                            Log.e("ExoPlayer", "TYPE_UNEXPECTED")
                        }
                        ExoPlaybackException.TIMEOUT_OPERATION_RELEASE ->{
                            //Toast.makeText(this@VideoService,"TIMEOUT_OPERATION_RELEASE ",Toast.LENGTH_LONG).show()
                            Log.e("ExoPlayer", "Error while releasing exoplayer")
                        }
                        ExoPlaybackException.TIMEOUT_OPERATION_SET_FOREGROUND_MODE ->{
                            //Toast.makeText(this@VideoService,"TIMEOUT_OPERATION_SET_FOREGROUND_MODE ",Toast.LENGTH_LONG).show()
                            Log.e("ExoPlayer", "TIMEOUT_OPERATION_SET_FOREGROUND_MODE ")
                        }
                        ExoPlaybackException.TIMEOUT_OPERATION_UNDEFINED ->{
                            //Toast.makeText(this@VideoService,"TIMEOUT_OPERATION_UNDEFINED ",Toast.LENGTH_LONG).show()
                            Log.e("ExoPlayer", "Exoplayer timeout operation undefined exception")
                        }
                        ExoPlaybackException.TYPE_OUT_OF_MEMORY->{
                            advertisementHelper.deleteCache()
                            //Toast.makeText(this@VideoService,"TYPE_OUT_OF_MEMORY ",Toast.LENGTH_LONG).show()
                            Log.e("ExoPlayer", "Exoplayer is out of memory clearing cache from system")
                        }
                        ExoPlaybackException.TYPE_REMOTE ->{
                            moveToNextVideo()
                            //Toast.makeText(this@VideoService,"TYPE_REMOTE ",Toast.LENGTH_LONG).show()
                            Log.e("ExoPlayer", "TYPE_REMOTE  ${error.message}")
                        }
                        ExoPlaybackException.TYPE_TIMEOUT->{
                            moveToNextVideo()
                            //Toast.makeText(this@VideoService,"TYPE_TIMEOUT ",Toast.LENGTH_LONG).show()
                            Log.e("ExoPlayer", "TYPE_TIMEOUT  ${error.timeoutException}")
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
        val dataSourceFactory: DataSource.Factory = CacheDataSource.Factory().setCache(AdvertisementsHelper.simpleCache).setUpstreamDataSourceFactory(DefaultHttpDataSourceFactory(Util.getUserAgent(this, getString(R.string.app_name)), DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true)).setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE)
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


