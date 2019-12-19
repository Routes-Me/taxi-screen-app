package com.example.routesapp.View.NewHomeScreen.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.routesapp.Class.Operations;
import com.example.routesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment {


    private Operations operations;

    //Define Advertisement Items ...
    private ImageView AD_Banner_ImageView;
    private VideoView AD_Video_VideoView;
    private TextView AD_Currencies_TextView;


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

        //Initialize Advertisement Items ...
        AD_Banner_ImageView = nMainView.findViewById(R.id.AD_Banner_ImageView);
        AD_Video_VideoView = nMainView.findViewById(R.id.AD_Video_VideoView);
        AD_Currencies_TextView = nMainView.findViewById(R.id.AD_Currencies_TextView);
        AD_Currencies_TextView.setSelected(true);


        operations = new Operations(getActivity(), AD_Video_VideoView, AD_Banner_ImageView, AD_Currencies_TextView);
        operations.fetchAdvertisementData();




    }

}
