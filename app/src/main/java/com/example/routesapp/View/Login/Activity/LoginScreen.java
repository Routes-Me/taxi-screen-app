package com.example.routesapp.View.Login.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.routesapp.R;
import com.example.routesapp.View.Login.LoginFragments.TabletDataFragment;
import com.example.routesapp.View.Login.LoginFragments.TechnicalLoginFragment;

public class LoginScreen extends AppCompatActivity {


    private Intent choose_Main_Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        initialize();


    }
    

    private void initialize() {

        //Main Fragment
        mainFragmentOfUserViewActivity();

    }


    private void mainFragmentOfUserViewActivity() {
        choose_Main_Fragment = getIntent();
        if (choose_Main_Fragment.hasExtra("Open_TabletDataFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new TabletDataFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new TechnicalLoginFragment()).commit();
        }
    }

}
