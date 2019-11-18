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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.routesapp.AndroidVideoCache.App;
import com.example.routesapp.AndroidVideoCache.Main3Activity;
import com.example.routesapp.FetchData.Interface.RoutesApi;
import com.example.routesapp.FetchData.Model.BannerModel;
import com.example.routesapp.FetchData.Model.BannersViewModel;
import com.example.routesapp.FetchData.Model.CurrenciesModel;
import com.example.routesapp.FetchData.Model.CurrenciesViewModel;
import com.example.routesapp.R;
import com.example.routesapp.FetchData.Model.VideoModel;
import com.example.routesapp.FetchData.Model.VideosViewModel;
import com.example.routesapp.View.Activity.MainActivity;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
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
    private int repeatVideoList = 0;


    //for ADS Images
    private Runnable r;
    private int currentImageIndex = 0;
    private int repeatImageList = 0;
    private RequestOptions options;

    private Activity activity;


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    // private String savedLanguage = null;
    private int Channel_Id = 2;


    //To Increase Video & Banner Views ....
    private OkHttpClient okHttpClient;
    private  Retrofit retrofit;
    private RoutesApi api;

    //For Video...
    private List<String> VideoList;
    private List<Integer> VideoViewList;
    private VideosViewModel videosViewModel;
    private VideoView ADS_VideoView;
    private ImageView ADS_VideoView_defaultImage;



    public SimpleExoPlayer exoPlayer;
    private BandwidthMeter bandwidthMeter;
    private TrackSelector trackSelector;
    private SimpleExoPlayerView ADS_exoPlayer_VideoView;
    private DefaultHttpDataSourceFactory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private MediaSource mediaSource;



    //For Banner...
    private List<String> BannerList;
    private List<Integer> BannerViewList;
    private BannersViewModel bannersViewModel;
    private ImageView ADS_ImageView;


    //For Currencies...
    private String CurrenciesString = "";
    private CurrenciesViewModel currenciesViewModel;
    private TextView scrollingtextMoney;



    public Operations(Activity activity, SimpleExoPlayerView ADS_exoPlayer_VideoView, VideoView ADS_VideoView, ImageView ADS_VideoView_defaultImage, ImageView ADS_ImageView, TextView scrollingtextMoney) {

       // mainActivity = new MainActivity();

        this.activity = activity;

        this.ADS_VideoView = ADS_VideoView;



        this.ADS_exoPlayer_VideoView= ADS_exoPlayer_VideoView;

        dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        extractorsFactory = new DefaultExtractorsFactory();

        this.ADS_VideoView_defaultImage = ADS_VideoView_defaultImage;
        this.ADS_ImageView = ADS_ImageView;
        this.scrollingtextMoney = scrollingtextMoney;



        okHttpClient = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES).readTimeout(30, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder().baseUrl(RoutesApi.BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(RoutesApi.class);



        //sharedPreference Storage
        this.sharedPreferences = activity.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        // this.savedLanguage = sharedPreferences.getString("Language", "English");


    }

    public Operations(Activity activity) {
        this.activity = activity;
    }







    public int getCurrentVideoIndex() {
        return currentVideoIndex;
    }

    public int getCurrentImageIndex() {
        return currentImageIndex;
    }


    //To play VideoModel of Product with MediaController..
    public void PlayVideo(List<Integer> videoViewId, final List<String> VideosList, final VideoView videoView) {

        //App.deleteCache(activity);

        if (currentVideoIndex < VideosList.size()) {



            try {

               // videoVisibility(false);
                //get Uri of Link(URL)
                Uri uri = Uri.parse(VideosList.get(currentVideoIndex));
                Increase_Video_View_Times(videoViewId.get(currentVideoIndex));

/*
                bandwidthMeter = new DefaultBandwidthMeter();

                trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

                exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
              //  Toast.makeText(activity, "Video Index: " + currentVideoIndex, Toast.LENGTH_SHORT).show();
                mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);

                ADS_exoPlayer_VideoView.setPlayer(exoPlayer);
                ADS_exoPlayer_VideoView.requestFocus();
                exoPlayer.prepare(mediaSource);
                try {
                    exoPlayer.setPlayWhenReady(true);
                    exoPlayer.getPlaybackState();
                }catch (Exception e){
                 //   Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                exoPlayer.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == ExoPlayer.STATE_ENDED){
                            //player back ended
                            currentVideoIndex++;
                            PlayVideo(VideoViewList, VideosList, videoView);
                        }

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity() {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }
                });
*/

                HttpProxyCacheServer proxy = App.getProxy(activity);
                String proxyUrl = proxy.getProxyUrl(String.valueOf(uri));
                videoView.setVideoPath(proxyUrl);

             //   videoView.setVideoURI(uri);

                MediaController mediaController = new MediaController(activity);
                mediaController.setAnchorView(videoView);

                videoView.setMediaController(mediaController);


                videoView.requestFocus();

                videoVisibility(true);
                videoView.start();

                //when video complete nothing do
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        // Toast.makeText(activity, "VideoModel Finished !", Toast.LENGTH_SHORT).show();

                        currentVideoIndex++;
                        PlayVideo(VideoViewList, VideosList, videoView);
                    }
                });

/*
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //start VideoModel and reStart when it is complete
                        //mp.setLooping(true);



                        //videoView.setDrawingCacheEnabled(true);

                        MediaController mediaController = new MediaController(activity);
                        mediaController.setAnchorView(videoView);

                        videoView.setMediaController(mediaController);


                        videoView.requestFocus();




                    }
                });
            */


            } catch (Exception ex) {
            }

            //videoView.requestFocus();

        } else {

           // mainActivity.tabletLocation(true);

            repeatVideoList++;
            currentVideoIndex = 0;
            //PlayVideo(VideosList, videoView);

            //Toast.makeText(activity, "Videos Finished!... Repeated : " + repeatVideoList + " times...", Toast.LENGTH_SHORT).show();

            //read data from API again...
            if (repeatVideoList < 5){
                PlayVideo(VideoViewList, VideosList, videoView);
            }else {
                repeatVideoList = 0;
                getVideosList();
            }

        }

    }


    //To Show ADS Images
    public void showADSImages(final List<Integer> bannerViewList, final List<String> BannersList, final ImageView ADSImageView) {

       // currentImageIndex = 0;
        options = new RequestOptions();
        options.centerCrop().fitCenter();
        r = new Runnable() {
            public void run() {

                if (currentImageIndex < BannersList.size()) {

                    Uri uri = Uri.parse(BannersList.get(currentImageIndex));
                    Increase_Banner_View_Times(bannerViewList.get(currentImageIndex));
                    try {
                        Glide.with(activity).load(uri).apply(options).into(ADSImageView);
                    } catch (Exception e) {
                    }
                    currentImageIndex++;

                    ADSImageView.postDelayed(r, 15000);

                }else {

                    repeatImageList++;

                    currentImageIndex = 0;

                   // Toast.makeText(activity, "Banners Finished!... Repeated : " + repeatImageList + " times...", Toast.LENGTH_SHORT).show();


                    //read data from API again...
                    if (repeatImageList < 5){
                        showADSImages(BannerViewList, BannersList, ADSImageView);
                    }else {
                        repeatImageList = 0;
                        getBannersList();
                    }


                }


            }
        };
        ADSImageView.postDelayed(r, 15000);
    }




    //Increase Video View Times ...
    private void Increase_Video_View_Times(final int videoViewsId) {

        api.IncreaseVideoViewTimes(videoViewsId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                      //  Toast.makeText(activity, "Video Id:  " + videoViewsId + " Increased ..." , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
             //   Toast.makeText(activity, "Error with increase ... Id:  "+ videoViewsId + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    //Increase Banner View Times ...
    private void Increase_Banner_View_Times(final int BannerViewsId) {

        api.IncreaseBannerViewTimes(BannerViewsId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(activity, "Banner Id:  " + BannerViewsId + " Increased ..." , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              //  Toast.makeText(activity, "Error with increase ... Id:  "+ BannerViewsId + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


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
                Glide.with(activity).load(image).into(imageView);
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
        }catch (Exception e){}

    }


    //To change Language
    public void setAppLocale(String language) {

        String localeCode = "en";

        switch (language) {

            case "English":
                localeCode = "en";
                break;

            case "Arabic":
                localeCode = "ar";
                break;

            case "Tagalog":
                localeCode = "phi";
                break;

            case "Urdu":
                localeCode = "hi";
                break;
        }

        try {

            Resources res = activity.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                conf.setLocale(new Locale(localeCode.toLowerCase()));
            } else {
                conf.locale = new Locale(localeCode.toLowerCase());
            }
            res.updateConfiguration(conf, dm);


            //  activity.recreate();

        } catch (Exception e) {
           // Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    //to get data by tablet Language..
    public void selectLang(String Lang, ImageView btn_selectLang) {
        switch (Lang) {

            case "English":
                btn_selectLang.setImageResource(R.drawable.english_flag);

                get_dataId_of_selectedLang(Lang);

                break;

            case "Arabic":
                btn_selectLang.setImageResource(R.drawable.kuwait_flag);

                get_dataId_of_selectedLang(Lang);

                break;

            case "Tagalog":
                btn_selectLang.setImageResource(R.drawable.philippines_flag);

                get_dataId_of_selectedLang(Lang);

                break;

            case "Urdu":
                btn_selectLang.setImageResource(R.drawable.india_flag);

                get_dataId_of_selectedLang(Lang);

                break;

        }
     //   mainActivity.showingLayout("counterLayout");
    }

    private void get_dataId_of_selectedLang(String lang) {
        // int test_id = 0;
        switch (lang) {

            case "English":
                Channel_Id = sharedPreferences.getInt("Video_En_Channel_ID", 0);
                break;

            case "Arabic":
                Channel_Id = sharedPreferences.getInt("Video_Ar_Channel_ID", 0);
                break;

            case "Tagalog":
                Channel_Id = sharedPreferences.getInt("Video_Ta_Channel_ID", 0);
                break;

            case "Urdu":
                Channel_Id = sharedPreferences.getInt("Video_Ur_Channel_ID", 0);
                break;

        }

        if (Channel_Id == 0) {
            Channel_Id = sharedPreferences.getInt("Video_En_Channel_ID", 0);
        }
        //Toast.makeText(this, "New Lan is: "+lang, Toast.LENGTH_SHORT).show();

        //get data from server

        try {
            getVideosList();
            getBannersList();
            scrollingTextView_Money();
        }catch (Exception e){}


    }



    private void getBannersList() {
        try {


            ADS_ImageView.setImageResource(R.drawable.bg_color);

            BannerList = new ArrayList<String>();
            BannerViewList = new ArrayList<Integer>();

            bannersViewModel = ViewModelProviders.of((FragmentActivity) activity).get(BannersViewModel.class);

            bannersViewModel.getBanners(Channel_Id, activity).observe((LifecycleOwner) activity, new Observer<List<BannerModel>>() {
                @Override
                public void onChanged(@Nullable List<BannerModel> BannersList) {

                    //Toast.makeText(activity, "New ID is : " + Channel_Id + " ,No of Banners: " + BannersList.size(), Toast.LENGTH_SHORT).show();


                    for (int Bno = 0; Bno < BannersList.size(); Bno++) {

                        BannerList.add(BannersList.get(Bno).getAdv_URL());
                        BannerViewList.add(BannersList.get(Bno).getAdv_ID());
                    }
                    showADSImages(BannerViewList, BannerList, ADS_ImageView);

                }


            });


        }catch (Exception e){}

    }
    private void getVideosList() {

      //  mainActivity.tabletLocation(true);

        try {

        videoVisibility(false);

        VideoList = new ArrayList<String>();
        VideoViewList = new ArrayList<Integer>();


        videosViewModel = ViewModelProviders.of((FragmentActivity) activity).get(VideosViewModel.class);

        videosViewModel.getVideos(Channel_Id,activity).observe((LifecycleOwner) activity, new Observer<List<VideoModel>>() {
            @Override
            public void onChanged(@Nullable List<VideoModel> VideosList) {

                //  Toast.makeText(MainActivity.this, "Changed Now", Toast.LENGTH_SHORT).show();

                // VideoList.clear();


             //  Toast.makeText(activity, "New ID is : " + Channel_Id + " ,No of Videos: " + VideosList.size(), Toast.LENGTH_SHORT).show();


                for (int Vno = 0; Vno < VideosList.size(); Vno++) {

                    //   Toast.makeText(MainActivity.this, "No: " + VideosList.get(0).getVideo_URL(), Toast.LENGTH_LONG).show();

                    VideoList.add(VideosList.get(Vno).getVideo_URL());
                    VideoViewList.add(VideosList.get(Vno).getVideo_ID());
                    //   Toast.makeText(MainActivity.this, "no. zero" + VideoList.get(0), Toast.LENGTH_SHORT).show();

                   //   Toast.makeText(MainActivity.this, "New ID is : " + test_id + " ,No of Videos: "+VideosList.size(), Toast.LENGTH_SHORT).show();

                }
                //  Toast.makeText(MainActivity.this, "no. of videos" + VideoList.size(), Toast.LENGTH_SHORT).show();
                PlayVideo(VideoViewList, VideoList, ADS_VideoView);



            }


        });

    }catch (Exception e){}

    }

    @SuppressLint("SetTextI18n")
    private void scrollingTextView_Money() {

        final int unicode = 0x1F4B0;

        try {

          //  CurrenciesList = new ArrayList<String>();

            currenciesViewModel = ViewModelProviders.of((FragmentActivity) activity).get(CurrenciesViewModel.class);

            currenciesViewModel.getCurrencies(1, activity).observe((LifecycleOwner) activity, new Observer<List<CurrenciesModel>>() {
                @Override
                public void onChanged(@Nullable List<CurrenciesModel> currenciesList) {

                   // Toast.makeText(activity, "New ID is : " + Channel_Id + " ,No of Banners: " + BannersList.size(), Toast.LENGTH_SHORT).show();


                    for (int Cno = 0; Cno < currenciesList.size(); Cno++) {
                    //    Toast.makeText(activity, "get Money!", Toast.LENGTH_SHORT).show();
                      String cur =   currenciesList.get(Cno).getCurrency_Name(activity) + " ( " +  currenciesList.get(Cno).getCurrency_Code() + " ) : " +  currenciesList.get(Cno).getCurrency_Eexchange_Rate() + " $   " + new String(Character.toChars(unicode)) ;

                      CurrenciesString  +=  cur   + "      ";

                    }

                    scrollingtextMoney.setText(CurrenciesString );

                   // showCurrencies(CurrenciesString);

                }


            });


        }catch (Exception e){}




    }


    @SuppressLint("SetTextI18n")
    private void showCurrencies(String currenciesString){

        int unicode = 0x1F60A;

        SpannableString spannableString = new SpannableString(currenciesString + "@");
        Drawable d = activity.getResources().getDrawable(R.drawable.icon_money);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        spannableString.setSpan(span, spannableString.toString().indexOf("@"),  spannableString.toString().indexOf("@")+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
       // yourTextView.setText(spannableString);



      //  SpannableString ss;
      //  Drawable d = null;
      //  ImageSpan span;
/*
        ss = new SpannableString(currenciesString);

        d = activity.getResources().getDrawable(R.drawable.icon_money);

        //d = getResources().getDrawable(R.drawable.load);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);



        //image start after text by 3 steps
        int imageStart = currenciesString.length() + 3;
        int imageEnd = imageStart + 3;
        ss.setSpan(span, imageStart, imageEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

*/

       // currenciesString = ss +"";

        scrollingtextMoney.setText(spannableString + new String(Character.toChars(unicode)));

    }

    private void videoVisibility(boolean isRunning){

        if (isRunning){

            ADS_VideoView.setVisibility(View.VISIBLE);
           // ADS_exoPlayer_VideoView.setVisibility(View.VISIBLE);
            ADS_VideoView_defaultImage.setVisibility(View.INVISIBLE);

        }else {

            ADS_VideoView.setVisibility(View.INVISIBLE);
          //  ADS_exoPlayer_VideoView.setVisibility(View.INVISIBLE);
            ADS_VideoView_defaultImage.setVisibility(View.VISIBLE);

        }

    }


}
