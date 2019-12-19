package com.example.routesapp.View.NewHomeScreen.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.routesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SideMenuFragment extends Fragment {



    private View nMainView;

    public SideMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.side_menu_fragment, container, false);

         initialize();

        return nMainView;


    }

    private void initialize() {
    }

}
