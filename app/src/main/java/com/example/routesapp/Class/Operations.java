package com.example.routesapp.Class;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.routesapp.Model.Advertisement;
import com.example.routesapp.Model.BannerModel;
import com.example.routesapp.Model.BannersViewModel;
import com.example.routesapp.Model.CurrenciesModel;
import com.example.routesapp.Model.CurrenciesViewModel;
import com.example.routesapp.R;
import com.example.routesapp.Model.VideoModel;
import com.example.routesapp.Model.VideosViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Operations {

    private Activity activity;

    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String  Bearer_TabletToken = null ,savedTabletToken = null;
    private int savedTabletChannelId = 0;


    //For Advertisement Banner ...
    private List<Advertisement> adBannerList;
    private BannersViewModel bannersViewModel;
    private ImageView ADS_ImageView;
    private Runnable r;
    private int currentImageIndex = 0;

    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true);


    //For Advertisement Video ...
    private CardView Advertisement_Video_CardView;
    private List<Advertisement> adVideoList;
    private VideosViewModel videosViewModel;
    private VideoView ADS_VideoView;
    private RingProgressBar videoRingProgressBar;
    private int currentVideoIndex = 0;
   // private MyAsync myAsync;
    private int duration = 0;
    private Timer timer;


    //For Advertisement Currencies...
    private CurrenciesViewModel currenciesViewModel;
    private TextView scrollingCurrencies_tv;
    private int currenciesUnicode = 0x1F4B0;
    private String CurrenciesString = "";



   //Constructor....
    public Operations(Activity activity,CardView Advertisement_Video_CardView , RingProgressBar videoRingProgressBar, VideoView ADS_VideoView, ImageView ADS_ImageView, TextView scrollingCurrencies_tv) {


        this.activity = activity;

        this.Advertisement_Video_CardView = Advertisement_Video_CardView;
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

        if (savedTabletToken != null && savedTabletChannelId > 0){
            Bearer_TabletToken = "Bearer " + savedTabletToken;
            try {

                fetchAdvertisementBannerList();

                //Old scrolling text
                //fetchAdvertisementCurrenciesList();

                //New scrolling text
                fetchAdvertisementScrollingText();

                fetchAdvertisementVideoList();

            }catch (Exception e){
                Crashlytics.logException(e);
            }
        }


    }
    private void fetchAdvertisementBannerList() {
        try {



            adBannerList = new ArrayList<Advertisement>();

            bannersViewModel = ViewModelProviders.of((FragmentActivity) activity).get(BannersViewModel.class);

            bannersViewModel.getBanners(savedTabletChannelId, activity,Bearer_TabletToken).observe((LifecycleOwner) activity, new Observer<List<BannerModel>>() {
                @Override
                public void onChanged(@Nullable List<BannerModel> BannersList) {

                    for (int Bno = 0; Bno < BannersList.size(); Bno++) {


                        adBannerList.add(new Advertisement(BannersList.get(Bno).getAdv_ID(),BannersList.get(Bno).getAdv_URL()));
                    }

                    displayAdvertisementBannerList(adBannerList);

                }


            });


        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }
    private void fetchAdvertisementVideoList() {

        try {

            adVideoList = new ArrayList<Advertisement>();

            videosViewModel = ViewModelProviders.of((FragmentActivity) activity).get(VideosViewModel.class);

            videosViewModel.getVideos(savedTabletChannelId,activity, Bearer_TabletToken).observe((LifecycleOwner) activity, new Observer<List<VideoModel>>() {
                @Override
                public void onChanged(@Nullable List<VideoModel> VideosList) {

                    for (int Vno = 0; Vno < VideosList.size(); Vno++) {


                        adVideoList.add(new Advertisement(VideosList.get(Vno).getVideo_ID(),VideosList.get(Vno).getVideo_URL()));
                    }
                    displayAdvertisementVideoList(adVideoList);

                }


            });

        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }
    @SuppressLint("SetTextI18n")
    private void fetchAdvertisementCurrenciesList() {
        try {
            currenciesViewModel = ViewModelProviders.of((FragmentActivity) activity).get(CurrenciesViewModel.class);

            currenciesViewModel.getCurrencies(1, activity, Bearer_TabletToken).observe((LifecycleOwner) activity, new Observer<List<CurrenciesModel>>() {
                @Override
                public void onChanged(@Nullable List<CurrenciesModel> currenciesList) {

                    for (int Cno = 0; Cno < currenciesList.size(); Cno++) {
                        String cur =   currenciesList.get(Cno).getCurrency_Name(activity) + " ( " +  currenciesList.get(Cno).getCurrency_Code() + " ) : " +  currenciesList.get(Cno).getCurrency_Eexchange_Rate() + " $   " + new String(Character.toChars(currenciesUnicode)) ;

                        CurrenciesString  +=  cur   + "      ";
                    }
                    displayAdvertisementCurrenciesList(CurrenciesString);
                }


            });

        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }

    @SuppressLint("SetTextI18n")
    private void fetchAdvertisementScrollingText() {
        try {
            currenciesViewModel = ViewModelProviders.of((FragmentActivity) activity).get(CurrenciesViewModel.class);

            currenciesViewModel.getCurrencies(1, activity, Bearer_TabletToken).observe((LifecycleOwner) activity, new Observer<List<CurrenciesModel>>() {
                @Override
                public void onChanged(@Nullable List<CurrenciesModel> currenciesList) {

                    for (int Cno = 0; Cno < currenciesList.size(); Cno++) {
                       // String cur =   currenciesList.get(Cno).getCurrency_Name(activity) + " ( " +  currenciesList.get(Cno).getCurrency_Code() + " ) : " +  currenciesList.get(Cno).getCurrency_Eexchange_Rate() + " $   " + new String(Character.toChars(currenciesUnicode)) ;

                        CurrenciesString  +=  currenciesList.get(Cno).getCurrency_Name(activity)   + "        ";
                    }
                    displayAdvertisementCurrenciesList(CurrenciesString);
                }


            });

        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }


    //Display advertisement data from server and display it ...

    //Display Advertisement Video with RingProgressBar
    private void displayAdvertisementVideoList(final List<Advertisement> adVideoList) {



      //  ADS_VideoView.getHolder().addCallback(activity);
       // Advertisement_Video_CardView.addView(ADS_VideoView);
// load video data in pvv.

        if (currentVideoIndex < adVideoList.size()) {

            try {
                Uri uri = Uri.parse(adVideoList.get(currentVideoIndex).getAdvertisement_URL());

                HttpProxyCacheServer proxy = App.getProxy(activity);
                String proxyUrl = proxy.getProxyUrl(String.valueOf(uri));
                ADS_VideoView.setVideoPath(proxyUrl);

                ADS_VideoView.requestFocus();

                //SetUp MediaController for VideoView ...
               // MediaController mediaController = new MediaController(activity);
               // mediaController.setAnchorView(ADS_VideoView);
               // ADS_VideoView.setMediaController(mediaController);

                videoRingProgressBar.setProgress(0);
                videoRingProgressBar.setMax(100);

                ADS_VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                       // Advertisement_Video_CardView.animate().xBy(25).setDuration(500).setInterpolator(new BounceInterpolator());
                       // Advertisement_Video_CardView.animate().rotation(15);

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


            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }

        } else {

            currentVideoIndex = 0;
            displayAdvertisementVideoList(adVideoList);

        }

    }
    private void timerCounter_videoRingProgressBar(){
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
    private void setDuration_videoRingProgressBar(){
        duration = ADS_VideoView.getDuration();
    }
    private void updateUI_videoRingProgressBar(){
        if (videoRingProgressBar.getProgress() >= 100) {
            timer.cancel();
        }
        int current = ADS_VideoView.getCurrentPosition();
        int progress = current * 100 / duration;
        videoRingProgressBar.setProgress(progress);
    }

    //Display Advertisement Banner
    private void displayAdvertisementBannerList(final List<Advertisement> adBannerList) {

       // final DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        r = new Runnable() {
            public void run() {

                if (currentImageIndex < adBannerList.size()) {


                   // fadeOutImageView(ADS_ImageView);

                    final Uri uri = Uri.parse(adBannerList.get(currentImageIndex).getAdvertisement_URL());

                    showBannerIntoImageView(uri);


                    currentImageIndex++;

                    if (currentImageIndex >= adBannerList.size()){
                        currentImageIndex = 0;
                    }


                    ADS_ImageView.postDelayed(r, 15400);

                }else {

                    
                   // currentImageIndex = 0;
                  //  displayAdvertisementBannerList(adBannerList);

                }


            }
        };
        ADS_ImageView.postDelayed(r, 1);
    }

    private void fadeOutImageView(final ImageView img) {

        Animation fadeOut = AnimationUtils.loadAnimation(activity,R.anim.fade_out);
        img.startAnimation(fadeOut);


    }

    private void showBannerIntoImageView(final Uri uri) {
        try {

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

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }


    //Display Advertisement Currencies
    private void displayAdvertisementCurrenciesList(String currenciesString){
        scrollingCurrencies_tv.setText(currenciesString );
    }





    //Enable / Disable Next Button ...
    public void enableNextButton(Button button, boolean enable){

        if (enable){
            button.setBackgroundResource(R.drawable.next_button_border_enable);
            button.setEnabled(true);
        }else {
            button.setBackgroundResource(R.drawable.next_button_border_disable);
            button.setEnabled(false);
        }

    }

    //To Hide Keyboard
    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }



    //Clickable items methods ....
    public void setTitle_In_textView(String Title_En, String Title_Ar, String Title_Or, String Title_Ta, String language, TextView textView){


        switch (language){

            case "English":

                textView.setText(Title_En);

                break;

            case "Arabic":

                textView.setText(Title_Ar);

                break;

            case "Urdu":

                textView.setText(Title_Or);

                break;

            case "Tagalog":

                textView.setText(Title_Ta);

                break;

            default:

                textView.setText(Title_En);

                break;

        }


    }
    public void setDiscount_In_textView(int discount, String language , TextView textView){

        if (discount > 0){

            switch (language){

                case "English":
                    textView.setText("Discount " + discount + "%");
                    break;

                case "Arabic":
                    textView.setText("خصم " + discount + "%");
                    break;

                case "Tagalog":
                    textView.setText("డిస్కౌంట్ " + discount + "%");
                    break;

                case "Urdu":
                    textView.setText("چھوٹ " + discount + "%");
                    break;

                default:
                    textView.setText("Discount " + discount + "%");
                    break;

            }

            textView.setVisibility(View.VISIBLE);

        }else {
            textView.setVisibility(View.GONE);
        }

    }
    public String getTitle_ofItems(String Title_En, String Title_Ar, String Title_Or, String Title_Ta, String language){

        String Title = Title_En;

        switch (language){

            case "English":

                Title = Title_En;

                break;

            case "Arabic":

                Title = Title_Ar;

                break;

            case "Urdu":

                Title = Title_Or;

                break;

            case "Tagalog":

                Title = Title_Ta;

                break;

            default:

                Title = Title_En;

                break;

        }

        return Title;
    }
    public String getDiscount_ofItems(int discount, String language ){

        String Discount = "Discount " + discount + "%";

        if (discount > 0){

            switch (language){

                case "English":
                    Discount = "Discount " + discount + "%";
                    break;

                case "Arabic":
                    Discount = "خصم " + discount + "%";
                    break;

                case "Tagalog":
                    Discount = "డిస్కౌంట్ " + discount + "%";
                    break;

                case "Urdu":
                    Discount = "چھوٹ " + discount + "%";
                    break;

                default:
                    Discount = "Discount " + discount + "%";
                    break;

            }


        }


        return  Discount;
    }
    public void setQRCodePic_In_imageView(String image, ImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        if (image != null) {
            if (!image.contains("https://")) {
                Glide.with(activity).load(R.drawable.qrcode).apply(options).into(imageView);
            } else {
                Glide.with(activity).load(image).apply(options).into(imageView);
            }
        }
    }
    public void setImage_In_imageView(String image, ImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.centerCrop().fitCenter();

        if (image != null) {
            if (image.contains("http://")) {
                Glide.with(activity).load(image).apply(options).into(imageView);

                // Glide.with(activity).load(image).apply(options).into(imageView);
            } else {
                // Glide.with(activity).load(image).apply(options).into(imageView);
            }
        }
    }

}
