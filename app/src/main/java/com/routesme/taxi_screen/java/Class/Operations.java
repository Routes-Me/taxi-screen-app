package com.routesme.taxi_screen.java.Class;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import com.routesme.taxi_screen.java.Model.Advertisement;
import com.routesme.taxi_screen.java.Model.BannerModel;
import com.routesme.taxi_screen.java.Model.BannersViewModel;
import com.routesme.taxi_screen.java.Model.CurrenciesModel;
import com.routesme.taxi_screen.java.Model.CurrenciesViewModel;
import com.routesme.taxi_screen.java.Model.VideoModel;
import com.routesme.taxi_screen.java.Model.VideosViewModel;
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
    //For Advertisement Banner ...
    private List<Advertisement> adBannerList;
    private BannersViewModel bannersViewModel;
    private ImageView ADS_ImageView;
    private Runnable r;
    private int currentImageIndex = 0;
    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true);
    //For Advertisement Video ...
    private List<Advertisement> adVideoList;
    private VideosViewModel videosViewModel;
    private VideoView ADS_VideoView;
    private RingProgressBar videoRingProgressBar;
    private int currentVideoIndex = 0;
    private int duration = 0;
    private Timer timer;
    //For Advertisement Currencies...
    private CurrenciesViewModel currenciesViewModel;
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
            fetchAdvertisementScrollingText();
            fetchAdvertisementVideoList();
        }

    }

    private void fetchAdvertisementBannerList() {
        adBannerList = new ArrayList<Advertisement>();
        bannersViewModel = ViewModelProviders.of((FragmentActivity) activity).get(BannersViewModel.class);
        bannersViewModel.getBanners(savedTabletChannelId, activity).observe((LifecycleOwner) activity, new Observer<List<BannerModel>>() {
            @Override
            public void onChanged(@Nullable List<BannerModel> BannersList) {
                for (int Bno = 0; Bno < BannersList.size(); Bno++) {
                    adBannerList.add(new Advertisement(BannersList.get(Bno).getAdv_ID(), BannersList.get(Bno).getAdv_URL()));
                }
                if (adBannerList != null && !adBannerList.isEmpty()) {
                    displayAdvertisementBannerList(adBannerList);
                }

            }


        });
    }

    private void fetchAdvertisementVideoList() {
        adVideoList = new ArrayList<Advertisement>();
        videosViewModel = ViewModelProviders.of((FragmentActivity) activity).get(VideosViewModel.class);
        videosViewModel.getVideos(savedTabletChannelId, activity).observe((LifecycleOwner) activity, new Observer<List<VideoModel>>() {
            @Override
            public void onChanged(@Nullable List<VideoModel> VideosList) {
                for (int Vno = 0; Vno < VideosList.size(); Vno++) {
                    adVideoList.add(new Advertisement(VideosList.get(Vno).getVideo_ID(), VideosList.get(Vno).getVideo_URL()));
                }
                if (adVideoList != null && !adVideoList.isEmpty()) {
                    displayAdvertisementVideoList(adVideoList);
                }

            }


        });
    }


    @SuppressLint("SetTextI18n")
    private void fetchAdvertisementScrollingText() {
        currenciesViewModel = ViewModelProviders.of((FragmentActivity) activity).get(CurrenciesViewModel.class);
        currenciesViewModel.getCurrencies(1, activity).observe((LifecycleOwner) activity, new Observer<List<CurrenciesModel>>() {
            @Override
            public void onChanged(@Nullable List<CurrenciesModel> currenciesList) {
                for (int Cno = 0; Cno < currenciesList.size(); Cno++) {
                    CurrenciesString += currenciesList.get(Cno).getCurrency_Name(activity) + "        ";
                }
                if (!CurrenciesString.isEmpty()) {
                    displayAdvertisementCurrenciesList(CurrenciesString);
                }
            }


        });
    }


    //Display advertisement data from server and display it ...

    //Display Advertisement Video with RingProgressBar
    private void displayAdvertisementVideoList(final List<Advertisement> adVideoList) {

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

    //Display Advertisement Banner
    private void displayAdvertisementBannerList(final List<Advertisement> adBannerList) {

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

    //Display Advertisement Currencies
    private void displayAdvertisementCurrenciesList(String currenciesString) {
        scrollingCurrencies_tv.setText(currenciesString);
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
