package com.routesme.taxi_screen.New_Hotspot_Configuration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.routesme.taxiscreen.R;

import java.util.List;

public class HotspotMainScreen extends PermissionsActivity {

    private static final String TAG = HotspotMainScreen.class.getSimpleName();
    private static final String SHOW_ICON = "show_icon" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_main_screen);




        final TextView instructTv = findViewById(R.id.instructions_tv);
        instructTv.setMovementMethod(LinkMovementMethod.getInstance());

        final TextView actionsNoteTv = findViewById(R.id.actions_note_tv);
        actionsNoteTv.setMovementMethod(LinkMovementMethod.getInstance());

        final TextView linkonTv = findViewById(R.id.linkon_tv);
        linkonTv.setMovementMethod(LinkMovementMethod.getInstance());

        final TextView linkoffTv = findViewById(R.id.linkoff_tv);
        linkoffTv.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Override
    public void onPermissionsOkay() {

    }


    public void onClickTurnOnAction(View v){
        Intent intent = new Intent(getString(R.string.intent_action_turnon));
        sendImplicitBroadcast(this,intent);
    }

    public void onClickTurnOffAction(View v){
        Intent intent = new Intent(getString(R.string.intent_action_turnoff));
        sendImplicitBroadcast(this,intent);
    }

    public void onClickTurnOnData(View v){
        MagicActivity.useMagicActivityToTurnOn(this);
    }

    public void onClickTurnOffData(View v){
        MagicActivity.useMagicActivityToTurnOff(this);
    }

    private static void sendImplicitBroadcast(Context ctxt, Intent i) {
        PackageManager pm=ctxt.getPackageManager();
        List<ResolveInfo> matches=pm.queryBroadcastReceivers(i, 0);

        for (ResolveInfo resolveInfo : matches) {
            Intent explicit=new Intent(i);
            ComponentName cn=
                    new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName,
                            resolveInfo.activityInfo.name);

            explicit.setComponent(cn);
            ctxt.sendBroadcast(explicit);
        }
    }

}
