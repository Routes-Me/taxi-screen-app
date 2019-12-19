package com.example.routesapp.View.NewHomeScreen.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.routesapp.R;
import com.example.routesapp.View.LastHomeScreen.Fragment.RecyclerViewFragment;
import com.example.routesapp.View.NewHomeScreen.Fragments.ContentFragment;
import com.example.routesapp.View.NewHomeScreen.Fragments.SideMenuFragment;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout homeScreenLayout;
    private boolean isLightTheme = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);


        initialize();

        showFragments();
    }

    private void initialize() {
        homeScreenLayout = findViewById(R.id.homeScreenLayout);
        homeScreenLayout.setOnClickListener(this);




    }


    //Fragment To Show [ RecyclerViewFragment    or    ViewItemFragment ]
    private void showFragments() {
        getSupportFragmentManager().beginTransaction().replace(R.id.contentFragment_container, new ContentFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.sideMenuFragment_container, new SideMenuFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.homeScreenLayout:
                //  changeTheme();
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void changeTheme() {
        if (isLightTheme){
            homeScreenLayout.setBackgroundColor(Color.parseColor("#000000"));
            isLightTheme = false;
        }else {
            homeScreenLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            isLightTheme = true;
        }
    }


}
