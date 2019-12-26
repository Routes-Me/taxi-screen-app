package com.routesme.taxi_screen.View.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.routesme.taxi_screen.Class.App;
import com.routesme.taxi_screen.Class.Operations;
import com.routesme.taxi_screen.Model.AuthCredentials;
import com.routesme.taxi_screen.Model.AuthCredentialsError;
import com.routesme.taxi_screen.Model.AuthCredentialsViewModel;
import com.routesme.taxiscreen.R;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.util.List;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    private Operations operations;

    private App app;



    private ImageView btnOpenLoginScreen;
    private RelativeLayout loginLayout;

    private long PressedTime;
    private Toast pressedTimesToast;
    private int clickTimes = 0;


    //For Login Layout...
    private String userName = null, password = null;
    View technical_login_screen ;
    private AuthCredentialsViewModel authCredentialsViewModel;
    private Button btn_next;
    private LinearLayout btn_learnMore;
    private EditText userName_et;
    private ShowHidePasswordEditText password_et;
    private TextView userName_error_tv, password_error_tv;
    private ProgressDialog dialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        initialize();

    }


    private void initialize() {

        operations = new Operations(this);

        app = (App) getApplicationContext();



        btnOpenLoginScreen = findViewById(R.id.btnOpenLoginScreen);
        btnOpenLoginScreen.setOnClickListener(this);
        loginLayout = findViewById(R.id.loginLayout);

        initializeLoginLayout();

        openLoginLayout(app.isNewLogin());
    }

    private void initializeLoginLayout() {
        technical_login_screen = findViewById(R.id.technical_login_screen);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        btn_next = technical_login_screen.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        operations.enableNextButton(btn_next,true);
        btn_learnMore = technical_login_screen.findViewById(R.id.btn_learnMore);
        btn_learnMore.setOnClickListener(this);

        userName_et = technical_login_screen.findViewById(R.id.userName_et);
        userName_error_tv = technical_login_screen.findViewById(R.id.userName_error_tv);
        password_et = technical_login_screen.findViewById(R.id.password_et);
        password_error_tv = technical_login_screen.findViewById(R.id.password_error_tv);
        editTextListener();



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpenLoginScreen:
                showLoginView();
                break;
            case R.id.btn_next:
                operations.enableNextButton(btn_next,false);
                saveAuthCredentials();
                openTaxiInformationScreen();
                break;
            case R.id.btn_learnMore:
                saveAuthCredentials();
                openLearnMoreScreen();
                break;
        }
    }

    private void openTaxiInformationScreen() {
        dialog.show();


        String userName = userName_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();



        AuthCredentials authCredentials = new AuthCredentials(userName, password);


        authCredentialsViewModel = ViewModelProviders.of((FragmentActivity) this).get(AuthCredentialsViewModel.class);
        authCredentialsViewModel.getToken(authCredentials,this, dialog).observe((LifecycleOwner) this, new Observer<List<AuthCredentialsError>>() {
            @Override
            public void onChanged(List<AuthCredentialsError> authCredentialsErrors) {

                dialog.dismiss();


                for (int e = 0 ; e < authCredentialsErrors.size() ; e++ ){
                    if (authCredentialsErrors.get(e).getErrorNumber() == 1 || authCredentialsErrors.get(e).getErrorNumber() == 2){
                        showErrorMessage(authCredentialsErrors.get(e).getErrorNumber(),authCredentialsErrors.get(e).getErrorMasseg(),true);
                      //  Toast.makeText(LoginScreen.this, "error id:  " + authCredentialsErrors.get(e).getErrorNumber(), Toast.LENGTH_SHORT).show();
                    }else {
                       // Toast.makeText(LoginScreen.this, "Error:  " + authCredentialsErrors.get(e).getErrorMasseg(), Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });
    }

    private void showLoginView() {


        try {
            clickTimes++;
            //if user Click on Back button Two Times
            if (PressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
              //  pressedTimesToast.cancel();

                openLoginLayout(true);

            }
            //if user Click on Back button One Times
            else {
               // pressedTimesToast = pressedTimesToast.makeText(getBaseContext(), "Clicked Times:  " + clickTimes, pressedTimesToast.LENGTH_SHORT);
              //  pressedTimesToast.show();
            }

            PressedTime = System.currentTimeMillis();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }


    }

    private void editTextListener() {
        userName_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showErrorMessage(1,"",false);
                operations.enableNextButton(btn_next,true);
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
                operations.enableNextButton(btn_next,true);
            }
            @Override
            public void afterTextChanged(Editable s) { }
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

    private void openLearnMoreScreen() {
        startActivity(new Intent(this, LearnMoreScreen.class));
    }

    private void openLoginLayout(boolean show){
        if (show) {
            btnOpenLoginScreen.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);

            userName = app.getTechnicalSupportUserName();
            password = app.getTechnicalSupportPassword();
            if (userName != null && !userName.isEmpty()){
                userName_et.setText(userName);
            }
            if (password != null && !password.isEmpty()){
                password_et.setText(password);
            }

        }else{
            btnOpenLoginScreen.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        }
    }


    private void saveAuthCredentials(){

        try {
            String userName = userName_et.getText().toString().trim();
            String password = password_et.getText().toString().trim();
            // if (!userName.equals(null) && !userName.isEmpty()){
            app.setTechnicalSupportUserName(userName);
            //  }
            //   if (!password.equals(null) && !password.isEmpty()){
            app.setTechnicalSupportPassword(password);
            //  }
            app.setNewLogin(true);
        }catch (Exception e){
            Crashlytics.logException(e);
        }



    }

}
