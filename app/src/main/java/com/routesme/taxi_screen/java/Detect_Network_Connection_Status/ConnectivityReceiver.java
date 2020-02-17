package com.routesme.taxi_screen.java.Detect_Network_Connection_Status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.routesme.taxi_screen.kotlin.Class.App;

import java.util.Objects;


public class ConnectivityReceiver extends BroadcastReceiver{


    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
       // super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }


    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(App.Companion.getInstance()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);

    }


}
