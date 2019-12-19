package com.example.routesapp.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true).centerCrop().fitCenter();


    //For Advertisement Video ...
    private List<Advertisement> adVideoList;
    private VideosViewModel videosViewModel;
    private VideoView ADS_VideoView;
    int currentVideoIndex = 0;


    //For Advertisement Currencies...
    private CurrenciesViewModel currenciesViewModel;
    private TextView scrollingCurrencies_tv;
    private int currenciesUnicode = 0x1F4B0;
    private String CurrenciesString = "";



   //Constructor....
    public Operations(Activity activity, VideoView ADS_VideoView, ImageView ADS_ImageView, TextView scrollingCurrencies_tv) {


        this.activity = activity;

        this.ADS_VideoView = ADS_VideoView;

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
                fetchAdvertisementCurrenciesList();
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

                    displayAdvertisementBannerList(adBannerList, ADS_ImageView);

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
                    displayAdvertisementVideoList(adVideoList, ADS_VideoView);

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


    //Display advertisement data from server and display it ...
    private void displayAdvertisementVideoList(final List<Advertisement> adVideoList, final VideoView videoView) {

        if (currentVideoIndex < adVideoList.size()) {

            try {
                Uri uri = Uri.parse(adVideoList.get(currentVideoIndex).getAdvertisement_URL());

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
                        displayAdvertisementVideoList(adVideoList, videoView);
                    }
                });





            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }


        } else {

            currentVideoIndex = 0;
            displayAdvertisementVideoList(adVideoList, videoView);

        }

    }
    private void displayAdvertisementBannerList(final List<Advertisement> adBannerList, final ImageView ADSImageView) {


        r = new Runnable() {
            public void run() {

                if (currentImageIndex < adBannerList.size()) {

                    Uri uri = Uri.parse(adBannerList.get(currentImageIndex).getAdvertisement_URL());
                    try {

                        Glide.with(activity).load(uri).apply(options).into(ADSImageView);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                    currentImageIndex++;

                    ADSImageView.postDelayed(r, 15000);

                }else {


                    currentImageIndex = 0;
                    displayAdvertisementBannerList(adBannerList, ADSImageView);

                }


            }
        };
        ADSImageView.postDelayed(r, 1);
    }
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
