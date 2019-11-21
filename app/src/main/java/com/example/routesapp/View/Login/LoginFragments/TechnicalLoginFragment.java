package com.example.routesapp.View.Login.LoginFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.routesapp.R;
import com.example.routesapp.View.Login.Activity.LearnMoreScreen;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalLoginFragment extends Fragment implements View.OnClickListener {



    private View nMainView;

    private TextView btn_next;
    private LinearLayout btn_learnMore;

    public TechnicalLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.technical_login_fragment, container, false);

        initialize();

        return nMainView;

    }

    private void initialize() {
        btn_next = nMainView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_learnMore = nMainView.findViewById(R.id.btn_learnMore);
        btn_learnMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                 openTabletDataFragment();
                break;
            case R.id.btn_learnMore:
                openLearnMoreScreen();
                break;
        }
    }

    private void openLearnMoreScreen() {
        startActivity(new Intent(getActivity(), LearnMoreScreen.class));
    }

    private void openTabletDataFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.login_fragment_container, new TabletDataFragment()).commit();    }
}
