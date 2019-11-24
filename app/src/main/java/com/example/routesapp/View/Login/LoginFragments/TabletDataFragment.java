package com.example.routesapp.View.Login.LoginFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.routesapp.R;
import com.example.routesapp.View.Activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabletDataFragment extends Fragment implements View.OnClickListener {



    private View nMainView;

    private ImageView btn_back;
    private TextView btn_done;

    public TabletDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.tablet_data_fragment, container, false);

        initialize();

        return nMainView;
    }

    private void initialize() {
        btn_back = nMainView.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        btn_done = nMainView.findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_back:
                openTechnicalLoginFragment();
                break;

            case R.id.btn_done:
                openMainActivity();
                break;

        }
    }

    private void openMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void openTechnicalLoginFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.login_fragment_container, new TechnicalLoginFragment()).commit();
    }
}
