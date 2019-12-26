package com.routesme.taxi_screen.View.NewHomeScreen.Fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.Class.Operations;
import com.routesme.taxi_screen.Model.ItemAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.routesme.taxiscreen.R;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment implements View.OnClickListener {


    private Operations operations;


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedTabletSerialNo = "";


    //Define Advertisement Items ...
    private ImageView AD_Banner_ImageView;
    private CardView Advertisement_Video_CardView;
    private VideoView AD_Video_VideoView;
    private RingProgressBar videoRingProgressBar;
    private TextView AD_Currencies_TextView;

    //Using Firebase Analytics ...
    private FirebaseAnalytics firebaseAnalytics;



    private View nMainView;

    public ContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.content_fragment, container, false);

        initialize();

        return nMainView;
    }

    private void initialize() {

        //sharedPreference Storage
        sharedPreferences = getActivity().getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedTabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);

        //Using Firebase Analytics ...
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        firebaseAnalytics.setUserId(savedTabletSerialNo);


        //Initialize Advertisement Items ...
        Advertisement_Video_CardView = nMainView.findViewById(R.id.Advertisement_Video_CardView);
        AD_Video_VideoView = nMainView.findViewById(R.id.AD_Video_VideoView);
        AD_Video_VideoView.setOnClickListener(this);
        videoRingProgressBar = nMainView.findViewById(R.id.videoRingProgressBar);
        AD_Banner_ImageView = nMainView.findViewById(R.id.AD_Banner_ImageView);
        AD_Banner_ImageView.setOnClickListener(this);
        AD_Currencies_TextView = nMainView.findViewById(R.id.AD_Currencies_TextView);
        AD_Currencies_TextView.setSelected(true);


        operations = new Operations(getActivity(),Advertisement_Video_CardView, videoRingProgressBar, AD_Video_VideoView, AD_Banner_ImageView, AD_Currencies_TextView);
        operations.fetchAdvertisementData();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.AD_Video_VideoView:
                updateFirebaseAnalystics(new ItemAnalytics(1, "click_video"));
                break;

            case R.id.AD_Banner_ImageView:
                updateFirebaseAnalystics(new ItemAnalytics(2, "click_banner"));
                break;

        }
    }


    private void updateFirebaseAnalystics(ItemAnalytics itemAnalytics) {
        try {
            //save into ( Custom Item Event )
            Bundle params = new Bundle();

            firebaseAnalytics.logEvent(itemAnalytics.getName(), params);
        }catch (Exception e){
            Crashlytics.logException(e);
        }


    }



}
