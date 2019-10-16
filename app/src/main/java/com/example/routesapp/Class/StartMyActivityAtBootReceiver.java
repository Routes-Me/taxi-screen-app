package com.example.routesapp.Class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.routesapp.View.Activity.MainActivity;




//Start App. at Boot Time

public class StartMyActivityAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        //Open MainActivity at Boot Time ....
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}
