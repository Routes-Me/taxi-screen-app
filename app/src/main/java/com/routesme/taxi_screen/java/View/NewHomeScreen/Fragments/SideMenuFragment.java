package com.routesme.taxi_screen.java.View.NewHomeScreen.Fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.routesme.taxi_screen.java.Class.CounterOperations;
import com.routesme.taxiscreen.R;

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
    }

    private void setUpTimeCounter() {
            counterOperations = new CounterOperations();
            timeClock_tv = nMainView.findViewById(R.id.timeClock_tv);
            timeDate_tv  = nMainView.findViewById(R.id.timeDate_tv);
            mTime = new Time();
            runnableTime = new Runnable() {
                @Override
                public void run() {
                    mTime.setToNow();
                    timeClock_tv.setText(counterOperations.getTimeClock(mTime));
                    timeDate_tv.setText(counterOperations.getDayOfWeek(mTime) + ", \n" + counterOperations.getDate(mTime));
                    handlerTime.postDelayed(runnableTime, 1000);
                }
            };
            handlerTime = new Handler();
            handlerTime.postDelayed(runnableTime, 1000);
    }
}
