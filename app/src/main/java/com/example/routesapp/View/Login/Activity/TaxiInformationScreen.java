package com.example.routesapp.View.Login.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.routesapp.Class.App;
import com.example.routesapp.R;
import com.example.routesapp.View.Activity.MainActivity;

public class TaxiInformationScreen extends AppCompatActivity {

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_information_screen);


        app = (App) getApplicationContext();
        Toast.makeText(this, "userName:  " + app.getTechnicalSupportName(), Toast.LENGTH_SHORT).show();

     //   startActivity(new Intent(this, MainActivity.class));
       // finish();

    }
}
