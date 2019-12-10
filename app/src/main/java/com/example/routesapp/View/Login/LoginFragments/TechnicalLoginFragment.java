package com.example.routesapp.View.Login.LoginFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routesapp.Model.AuthCredentials;
import com.example.routesapp.Model.AuthCredentialsError;
import com.example.routesapp.Model.AuthCredentialsViewModel;
import com.example.routesapp.R;
import com.example.routesapp.View.Login.Activity.LearnMoreScreen;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalLoginFragment extends Fragment implements View.OnClickListener {




    private AuthCredentialsViewModel authCredentialsViewModel;

    private View nMainView;

    private TextView btn_next;
    private LinearLayout btn_learnMore;

    private EditText userName_et;
    private ShowHidePasswordEditText password_et;
    private TextView userName_error_tv, password_error_tv;

    private ProgressDialog dialog;


    public TechnicalLoginFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.technical_login_layout, container, false);

        initialize();



        return nMainView;


    }



    private void initialize() {


        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        btn_next = nMainView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        enableNextButton(true);
        btn_learnMore = nMainView.findViewById(R.id.btn_learnMore);
        btn_learnMore.setOnClickListener(this);

        userName_et = nMainView.findViewById(R.id.email_et);
        userName_error_tv = nMainView.findViewById(R.id.userName_error_tv);
        password_et = nMainView.findViewById(R.id.password_et);
        password_error_tv = nMainView.findViewById(R.id.password_error_tv);
        editTextListener();


    }

    private void editTextListener() {
        userName_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showErrorMessage(1,"",false);
                enableNextButton(true);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showErrorMessage(2,"",false);
                enableNextButton(true);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                enableNextButton(false);
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


        dialog.show();


       String userName = userName_et.getText().toString().trim();
       String password = password_et.getText().toString().trim();


        AuthCredentials authCredentials = new AuthCredentials(userName, password);
        authCredentialsViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(AuthCredentialsViewModel.class);
        authCredentialsViewModel.getToken(authCredentials,getActivity(), dialog).observe((LifecycleOwner) getActivity(), new Observer<List<AuthCredentialsError>>() {
                    @Override
                    public void onChanged(List<AuthCredentialsError> authCredentialsErrors) {

                        dialog.dismiss();


                            for (int e = 0 ; e < authCredentialsErrors.size() ; e++ ){
                                if (authCredentialsErrors.get(e).getErrorNumber() == 1 || authCredentialsErrors.get(e).getErrorNumber() == 2){
                                    showErrorMessage(authCredentialsErrors.get(e).getErrorNumber(),authCredentialsErrors.get(e).getErrorMasseg(),true);
                                }else {
                                    Toast.makeText(getActivity(), "Error:  " + authCredentialsErrors.get(e).getErrorMasseg(), Toast.LENGTH_SHORT).show();
                                }
                            }



                    }
                });

    }


    private void showErrorMessage(int errorId , String errorStr, boolean show){

        EditText editText = null;
        TextView textView = null;

        switch (errorId){

            case 1 :
                editText = userName_et;
                textView = userName_error_tv;
                break;

            case 2 :
                editText = password_et;
                textView = password_error_tv;
                break;
        }


        if (show){
            editText.setBackgroundResource(R.drawable.red_border);
            textView.setText("* " + errorStr);
            textView.setVisibility(View.VISIBLE);
            return;
        }else {
            editText.setBackgroundResource(R.drawable.grey_border_edit_text);
            textView.setVisibility(View.INVISIBLE);
        }



    }




   private void enableNextButton(boolean enable){

        if (enable){
            btn_next.setBackgroundResource(R.drawable.next_button_border_enable);
            btn_next.setEnabled(true);
        }else {
            btn_next.setBackgroundResource(R.drawable.next_button_border_disable);
            btn_next.setEnabled(false);
        }

   }





}
