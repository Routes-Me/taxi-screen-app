package com.example.routesapp.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.crashlytics.android.Crashlytics;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.routesapp.Class.App;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.Model.Advertisement;
import com.example.routesapp.Model.BannerModel;
import com.example.routesapp.Model.BannersViewModel;
import com.example.routesapp.Model.CurrenciesModel;
import com.example.routesapp.Model.CurrenciesViewModel;
import com.example.routesapp.Model.ItemsModel;
import com.example.routesapp.Model.ItemsViewModel;
import com.example.routesapp.R;
import com.example.routesapp.Model.VideoModel;
import com.example.routesapp.Model.VideosViewModel;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Operations {


    //for Videos
    int currentVideoIndex = 0;



    //for ADS Images
    private Runnable r;
    private int currentImageIndex = 0;

    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true).centerCrop().fitCenter();

    private Activity activity;


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;

    private String savedToken = null;
    private int Channel_Id = 2;




    //For Video...
    private List<String> VideoList;
    private List<Integer> VideoViewList;
    private VideosViewModel videosViewModel;
    private VideoView ADS_VideoView;






    //For Banner...
    private List<String> BannerList;
    private List<Integer> BannerViewList;
    private BannersViewModel bannersViewModel;
    private ImageView ADS_ImageView;


    //For Currencies...
    private String CurrenciesString = "";
    private CurrenciesViewModel currenciesViewModel;
    private TextView scrollingtextMoney;






    public Operations(Activity activity, VideoView ADS_VideoView, ImageView ADS_ImageView, TextView scrollingtextMoney) {


        this.activity = activity;

        this.ADS_VideoView = ADS_VideoView;

        this.ADS_ImageView = ADS_ImageView;
        this.scrollingtextMoney = scrollingtextMoney;



        //sharedPreference Storage
        this.sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedToken = "Bearer " + sharedPreferences.getString("tabToken", null);

    }

    public Operations(Activity activity) {
        this.activity = activity;
    }





    //To play VideoModel of Product with MediaController..
    public void PlayVideo(final List<Integer> videoViewId, final List<String> VideosList, final VideoView videoView) {


        if (currentVideoIndex < VideosList.size()) {



            try {

                Uri uri = Uri.parse(VideosList.get(currentVideoIndex));




                HttpProxyCacheServer proxy = App.getProxy(activity);
                String proxyUrl = proxy.getProxyUrl(String.valueOf(uri));
                videoView.setVideoPath(proxyUrl);



                videoView.requestFocus();

                videoView.start();

                //when video complete nothing do
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        currentVideoIndex++;
                        PlayVideo(videoViewId, VideosList, videoView);
                    }
                });



            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }


        } else {


            currentVideoIndex = 0;
            PlayVideo(videoViewId, VideosList, videoView);

        }

    }


    //To Show ADS Images
    public void showADSImages(final List<Integer> bannerViewList, final List<String> BannersList, final ImageView ADSImageView) {


        r = new Runnable() {
            public void run() {

                if (currentImageIndex < BannersList.size()) {

                    Uri uri = Uri.parse(BannersList.get(currentImageIndex));
                    try {

                        Glide.with(activity).load(uri).apply(options).into(ADSImageView);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                    currentImageIndex++;

                    ADSImageView.postDelayed(r, 15000);

                }else {


                    currentImageIndex = 0;
                    showADSImages(bannerViewList, BannersList, ADSImageView);



                }


            }
        };
        ADSImageView.postDelayed(r, 1);
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



    //To Hide Keyboard
    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }







    public void get_dataId_of_selectedLang() {

        try {

            Channel_Id = sharedPreferences.getInt("tabletChannelId", 0);

            getBannersList();
            scrollingTextView_Money();
            getVideosList();

        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }



    private void getBannersList() {
        try {


            ADS_ImageView.setImageResource(R.drawable.bg_color);

            BannerList = new ArrayList<String>();
            BannerViewList = new ArrayList<Integer>();

            bannersViewModel = ViewModelProviders.of((FragmentActivity) activity).get(BannersViewModel.class);

            bannersViewModel.getBanners(Channel_Id, activity,savedToken).observe((LifecycleOwner) activity, new Observer<List<BannerModel>>() {
                @Override
                public void onChanged(@Nullable List<BannerModel> BannersList) {



                    for (int Bno = 0; Bno < BannersList.size(); Bno++) {

                        BannerList.add(BannersList.get(Bno).getAdv_URL());
                        BannerViewList.add(BannersList.get(Bno).getAdv_ID());
                    }

                    showADSImages(BannerViewList, BannerList, ADS_ImageView);

                }


            });


        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }
    private void getVideosList() {

        try {

            VideoList = new ArrayList<String>();
            VideoViewList = new ArrayList<Integer>();


            videosViewModel = ViewModelProviders.of((FragmentActivity) activity).get(VideosViewModel.class);

            videosViewModel.getVideos(Channel_Id,activity, savedToken).observe((LifecycleOwner) activity, new Observer<List<VideoModel>>() {
                @Override
                public void onChanged(@Nullable List<VideoModel> VideosList) {

                    for (int Vno = 0; Vno < VideosList.size(); Vno++) {

                        VideoList.add(VideosList.get(Vno).getVideo_URL());
                        VideoViewList.add(VideosList.get(Vno).getVideo_ID());

                    }
                    PlayVideo(VideoViewList, VideoList, ADS_VideoView);

                }


            });

        }catch (Exception e){
            Crashlytics.logException(e);
        }

    }

    @SuppressLint("SetTextI18n")
    private void scrollingTextView_Money() {

        final int unicode = 0x1F4B0;

        try {

            currenciesViewModel = ViewModelProviders.of((FragmentActivity) activity).get(CurrenciesViewModel.class);

            currenciesViewModel.getCurrencies(1, activity, savedToken).observe((LifecycleOwner) activity, new Observer<List<CurrenciesModel>>() {
                @Override
                public void onChanged(@Nullable List<CurrenciesModel> currenciesList) {

                    for (int Cno = 0; Cno < currenciesList.size(); Cno++) {
                        String cur =   currenciesList.get(Cno).getCurrency_Name(activity) + " ( " +  currenciesList.get(Cno).getCurrency_Code() + " ) : " +  currenciesList.get(Cno).getCurrency_Eexchange_Rate() + " $   " + new String(Character.toChars(unicode)) ;

                        CurrenciesString  +=  cur   + "      ";
                    }

                    scrollingtextMoney.setText(CurrenciesString );

                }


            });

        }catch (Exception e){
            Crashlytics.logException(e);
        }




    }








    public void enableNextButton(Button button, boolean enable){

        if (enable){
            button.setBackgroundResource(R.drawable.next_button_border_enable);
            button.setEnabled(true);
        }else {
            button.setBackgroundResource(R.drawable.next_button_border_disable);
            button.setEnabled(false);
        }

    }

}
