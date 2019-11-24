package com.example.routesapp.View.Login.LoginFragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.routesapp.Class.AES;
import com.example.routesapp.R;
import com.example.routesapp.View.Login.Activity.LearnMoreScreen;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalLoginFragment extends Fragment implements View.OnClickListener {


    private static  String originalString = "Abdullah Soubeih";

    private AES aes;

    private View nMainView;

    private TextView btn_next;
    private LinearLayout btn_learnMore;

    private EditText email_et;
    private ShowHidePasswordEditText password_et;
    private TextView email_error_tv, password_error_tv;

    public TechnicalLoginFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.technical_login_fragment, container, false);

        initialize();


        //Using AES 256 To Encryption & Decryption ...
      //  Toast.makeText(getActivity(), "encrypt:   "+ AES.encrypt(originalString)    +  "   ,decrypt:  " + AES.decrypt(AES.encrypt(originalString)), Toast.LENGTH_SHORT).show();

        return nMainView;


       // aes = new AES(getActivity());



        //String decryptedString = AES.decrypt(encryptedString, secretKey) ;


    }

    private void initialize() {
        btn_next = nMainView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_learnMore = nMainView.findViewById(R.id.btn_learnMore);
        btn_learnMore.setOnClickListener(this);

        email_et = nMainView.findViewById(R.id.email_et);
        email_error_tv = nMainView.findViewById(R.id.email_error_tv);
        password_et = nMainView.findViewById(R.id.password_et);
        password_error_tv = nMainView.findViewById(R.id.password_error_tv);
        editTextListener();


    }

    private void editTextListener() {
        email_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showErrorMessage(email_et, email_error_tv,"",false);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showErrorMessage(password_et, password_error_tv,"",false);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
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


        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();



        if (email.isEmpty()){
           // email_et.setError("Email Address Required");
          //  email_et.requestFocus();

            showErrorMessage(email_et, email_error_tv,"* Email Address Required",true);

            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
          //  email_et.setError("Enter Valid Email");
          //  email_et.requestFocus();
            showErrorMessage(email_et, email_error_tv,"* Enter Valid Email",true);
            return;
        }

        if (password.isEmpty()){
           // password_et.setError("Password Required");
           // password_et.requestFocus();

            showErrorMessage(password_et, password_error_tv,"* Password Required",true);

            return;
        }

        if (password.length() < 8){
          //  password_et.setError("Minimum Password is 8 digit");
          //  password_et.requestFocus();

            showErrorMessage(password_et, password_error_tv,"* Minimum Password is 8 digit",true);

            return;
        }


        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.login_fragment_container, new TabletDataFragment()).commit();
    }


    private void showErrorMessage(EditText editText,TextView errorTv, String errorStr, boolean show){

        if (show){
            editText.setBackgroundResource(R.drawable.red_border);
            errorTv.setText(errorStr);
            errorTv.setVisibility(View.VISIBLE);
        }else {
            editText.setBackgroundResource(R.drawable.grey_border);
            errorTv.setVisibility(View.INVISIBLE);
        }



    }










}
