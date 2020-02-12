package com.routesme.taxi_screen.java.Class;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.routesme.taxi_screen.java.Model.BannersViewModel;
import com.routesme.taxi_screen.kotlin.Model.BannerModel;
import com.routesme.taxi_screen.kotlin.Model.VideoModel;
import com.routesme.taxi_screen.kotlin.ViewModel.ViewModel;
import com.routesme.taxiscreen.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;


public class Operations {

    private Activity activity;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedTabletToken = null;
    private int savedTabletChannelId = 0;

    private List<BannerModel> adBannerList;
    private BannersViewModel bannersViewModel;
    private ImageView ADS_ImageView;
    private Runnable r;
    private int currentImageIndex = 0;
    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true);

    private List<VideoModel> adVideoList;
    private ViewModel videosViewModel;
    private VideoView ADS_VideoView;
    private RingProgressBar videoRingProgressBar;
    private int currentVideoIndex = 0;
    private int duration = 0;
    private Timer timer;

    private TextView scrollingCurrencies_tv;
    private String CurrenciesString = "";


    //Constructor....
    public Operations(Activity activity, RingProgressBar videoRingProgressBar, VideoView ADS_VideoView, ImageView ADS_ImageView, TextView scrollingCurrencies_tv) {
        this.activity = activity;
        this.ADS_VideoView = ADS_VideoView;
        this.videoRingProgressBar = videoRingProgressBar;
        this.ADS_ImageView = ADS_ImageView;
        this.scrollingCurrencies_tv = scrollingCurrencies_tv;
        //sharedPreference Storage
        this.sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedTabletToken = sharedPreferences.getString("tabToken", null);
        savedTabletChannelId = sharedPreferences.getInt("tabletChannelId", 0);
    }

    public Operations(Activity activity) {
        this.activity = activity;
    }

    //Fetch advertisement data from server to display it ...
    public void fetchAdvertisementData() {

        if (savedTabletToken != null && savedTabletChannelId > 0) {
            fetchAdvertisementBannerList();
            fetchAdvertisementVideoList();
        }

    }

    private void fetchAdvertisementBannerList() {
        adBannerList = new ArrayList<BannerModel>();
        bannersViewModel = ViewModelProviders.of((FragmentActivity) activity).get(BannersViewModel.class);
        bannersViewModel.getBanners(savedTabletChannelId, activity).observe((LifecycleOwner) activity, new Observer<List<BannerModel>>() {
            @Override
            public void onChanged(@Nullable List<BannerModel> BannersList) {
                for (int Bno = 0; Bno < BannersList.size(); Bno++) {
                    adBannerList.add(new BannerModel(BannersList.get(Bno).getAdvertisement_ID(), BannersList.get(Bno).getAdvertisement_URL()));
                }
                if (adBannerList != null && !adBannerList.isEmpty()) {
                    displayAdvertisementBannerList(adBannerList);
                }

            }


        });
    }

    private void fetchAdvertisementVideoList() {
        adVideoList = new ArrayList<VideoModel>();
        videosViewModel = ViewModelProviders.of((FragmentActivity) activity).get(ViewModel.class);
        videosViewModel.getVideoList(savedTabletChannelId, activity).observe((LifecycleOwner) activity, new Observer<List<VideoModel>>() {
            @Override
            public void onChanged(@Nullable List<VideoModel> VideosList) {
                for (int Vno = 0; Vno < VideosList.size(); Vno++) {
                    adVideoList.add(new VideoModel(VideosList.get(Vno).getAdvertisement_ID(), VideosList.get(Vno).getAdvertisement_URL()));
                }
                if (adVideoList != null && !adVideoList.isEmpty()) {
                    displayAdvertisementVideoList(adVideoList);
                }

            }


        });
    }





    //Display advertisement data from server and display it ...

    private void displayAdvertisementVideoList(final List<VideoModel> adVideoList) {

        if (currentVideoIndex < adVideoList.size()) {
            Uri uri = Uri.parse(adVideoList.get(currentVideoIndex).getAdvertisement_URL());

            HttpProxyCacheServer proxy = App.getProxy(activity);
            String proxyUrl = proxy.getProxyUrl(String.valueOf(uri));
            ADS_VideoView.setVideoPath(proxyUrl);
            ADS_VideoView.requestFocus();
            videoRingProgressBar.setProgress(0);
            videoRingProgressBar.setMax(100);

            ADS_VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    ADS_VideoView.start();
                    setDuration_videoRingProgressBar();
                    timerCounter_videoRingProgressBar();
                }
            });

            //when video complete
            ADS_VideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentVideoIndex++;
                    displayAdvertisementVideoList(adVideoList);
                }
            });

        } else {
            currentVideoIndex = 0;
            displayAdvertisementVideoList(adVideoList);
        }

    }

    private void timerCounter_videoRingProgressBar() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI_videoRingProgressBar();
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void setDuration_videoRingProgressBar() {
        duration = ADS_VideoView.getDuration();
    }

    private void updateUI_videoRingProgressBar() {
        if (videoRingProgressBar.getProgress() >= 100) {
            timer.cancel();
        }
        int current = ADS_VideoView.getCurrentPosition();
        int progress = current * 100 / duration;
        videoRingProgressBar.setProgress(progress);
    }

    private void displayAdvertisementBannerList(final List<BannerModel> adBannerList) {

        r = new Runnable() {
            public void run() {

                if (currentImageIndex < adBannerList.size()) {

                    final Uri uri = Uri.parse(adBannerList.get(currentImageIndex).getAdvertisement_URL());
                    showBannerIntoImageView(uri);
                    currentImageIndex++;
                    if (currentImageIndex >= adBannerList.size()) {
                        currentImageIndex = 0;
                    }
                    ADS_ImageView.postDelayed(r, 15400);
                }
            }
        };
        ADS_ImageView.postDelayed(r, 1);
    }

    private void showBannerIntoImageView(final Uri uri) {
        final ObjectAnimator animation1 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 1f, 0f).setDuration(200);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(ADS_ImageView, "scaleY", 0f, 1f).setDuration(200);
        animation1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());

        animation1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Glide.with(activity).load(uri).apply(options).into(ADS_ImageView);
                oa2.start();
            }
        });
        animation1.start();
    }


    //Enable / Disable Next Button ...
    public void enableNextButton(Button button, boolean enable) {
        if (enable) {
            button.setBackgroundResource(R.drawable.next_button_border_enable);
            button.setEnabled(true);
        } else {
            button.setBackgroundResource(R.drawable.next_button_border_disable);
            button.setEnabled(false);
        }
    }

}
