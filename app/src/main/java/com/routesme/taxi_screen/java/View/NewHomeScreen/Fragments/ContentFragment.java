package com.routesme.taxi_screen.java.View.NewHomeScreen.Fragments;


import android.app.Activity;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import com.routesme.taxi_screen.java.Class.App;
import com.routesme.taxi_screen.java.Class.Operations;
import com.routesme.taxi_screen.java.Detect_Network_Connection_Status.ConnectivityReceiver;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.routesme.taxi_screen.kotlin.Model.ItemAnalytics;
import com.routesme.taxiscreen.R;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "ContentFragment";

    private Operations operations;


    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedTabletSerialNo = "";



    private ImageView AD_Banner_ImageView;
    private VideoView AD_Video_VideoView;
    private RingProgressBar videoRingProgressBar;
    private TextView AD_Currencies_TextView;

    //Using Firebase Analytics ...
    private FirebaseAnalytics firebaseAnalytics;

    private ConnectivityReceiver connectionReceiver;
    private IntentFilter intentFilter;

    private View nMainView;

    //Detect Internet Status ...
    private boolean isConnected = false, isDataFetched = false;

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
        sharedPreferences = getActivity().getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedTabletSerialNo = sharedPreferences.getString("tabletSerialNo", null);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        firebaseAnalytics.setUserId(savedTabletSerialNo);
        AD_Video_VideoView = nMainView.findViewById(R.id.AD_Video_VideoView);
      //  AD_Video_VideoView.setOnClickListener(this);
        videoRingProgressBar = nMainView.findViewById(R.id.videoRingProgressBar);
        AD_Banner_ImageView = nMainView.findViewById(R.id.AD_Banner_ImageView);
      //  AD_Banner_ImageView.setOnClickListener(this);
        AD_Currencies_TextView = nMainView.findViewById(R.id.AD_Currencies_TextView);
        AD_Currencies_TextView.setSelected(true);
        operations = new Operations(getActivity(), videoRingProgressBar, AD_Video_VideoView, AD_Banner_ImageView, AD_Currencies_TextView);
        connectionReceiver = new ConnectivityReceiver();
        checkConnection();
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
        //save into ( Custom Item Event )
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent(itemAnalytics.getName(), params);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ConnectivityReceiverRegistering(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectivityReceiverRegistering(false);
    }


    // Method to manually check connection status
    private void checkConnection() {
        isConnected = ConnectivityReceiver.isConnected();
        Log.d(TAG, "ContentFragment .. Initial ... Internet Connection Status:  " + isConnected);
        if (isConnected) {
            operations.fetchAdvertisementData();
            isDataFetched = true;
        } else {
            networkListener();
        }
    }

    private void networkListener() {
        ConnectivityReceiverRegistering(true);
        App.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        Log.d(TAG, "ContentFragment .. Changed ... Internet Connection Status:  " + isConnected);
        if (isConnected && !isDataFetched) {
            operations.fetchAdvertisementData();
            isDataFetched = true;
            ConnectivityReceiverRegistering(false);
        }
    }


    private void ConnectivityReceiverRegistering(boolean register) {
        try {
            // register or unRegister your broadcast connectionReceiver
            if (register) {
                intentFilter = new IntentFilter("com.routesme.taxi_screen.SOME_ACTION");
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                getActivity().registerReceiver(connectionReceiver, intentFilter);
            } else {
                getActivity().unregisterReceiver(connectionReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
