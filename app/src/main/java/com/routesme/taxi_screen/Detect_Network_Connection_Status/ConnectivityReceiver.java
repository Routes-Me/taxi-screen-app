package com.routesme.taxi_screen.Detect_Network_Connection_Status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.routesme.taxi_screen.Class.App;

public class ConnectivityReceiver extends BroadcastReceiver{
    private static final String TAG = "ConnectivityReceiver";

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
       // super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //  Log.d(TAG, "From Connectivity Receiver ... Changed ... Internet Connection Status:  [ onReceive ] " );

        //Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
          //  Log.d(TAG, "From Connectivity Receiver ... Changed ... Internet Connection Status:  "+ isConnected);
        }
    }



    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);

    }


}
