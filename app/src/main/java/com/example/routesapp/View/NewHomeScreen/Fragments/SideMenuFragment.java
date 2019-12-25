package com.example.routesapp.View.NewHomeScreen.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.routesapp.Class.CounterOperations;
import com.example.routesapp.R;
import com.example.routesapp.View.LastHomeScreen.Fragment.RecyclerViewFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SideMenuFragment extends Fragment {



    //for Time Counter....
    private CounterOperations counterOperations;
    private Time mTime;
    private Handler handlerTime;
    private Runnable runnableTime;
    private TextView timeClock_tv, timeDate_tv;



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


        setUpTimeCounter();

        mainFragmentToShow();


    }


    //Fragment To Show [ RecyclerViewFragment    or    ViewItemFragment ]
    private void mainFragmentToShow() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_clickableItems, new RecyclerViewFragment()).commit();
    }

    private void setUpTimeCounter() {
        try {
            counterOperations = new CounterOperations(getActivity());

            timeClock_tv = nMainView.findViewById(R.id.timeClock_tv);
            timeDate_tv  = nMainView.findViewById(R.id.timeDate_tv);

            mTime = new Time();

            runnableTime = new Runnable() {
                @Override
                public void run() {

                    mTime.setToNow();

                    // counterOperations.getCurrentTime_newView(mTime, timeClock_tv, timeDate_tv);

                    timeClock_tv.setText(counterOperations.getTimeClock(mTime));
                    timeDate_tv.setText(counterOperations.getDayOfWeek(mTime) + ", \n" + counterOperations.getDate(mTime));


                    handlerTime.postDelayed(runnableTime, 1000);

                }
            };

            handlerTime = new Handler();
            handlerTime.postDelayed(runnableTime, 1000);
        }catch (Exception e){
            Crashlytics.logException(e);
        }



    }





}
