package com.routesme.taxi_screen.java.View.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.routesme.taxi_screen.java.Class.App;
import com.routesme.taxi_screen.java.Class.Operations;
import com.routesme.taxi_screen.java.Model.AuthCredentialsViewModel;
import com.routesme.taxi_screen.kotlin.Model.AuthCredentials;
import com.routesme.taxi_screen.kotlin.Model.AuthCredentialsError;
import com.routesme.taxiscreen.R;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import java.util.List;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    private Operations operations;
    private App app;
    private ImageView btnOpenLoginScreen;
    private RelativeLayout loginLayout;
    private long PressedTime;
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
    private String final_pattern = "";
    private Dialog exitPatternDialog;
    private PatternLockView pattern_exitApp;
    private ImageView openPattern;
    private String patternPassword;




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
        openPattern = findViewById(R.id.openPattern);
        openPattern.setOnClickListener(this);
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

            case R.id.openPattern:
                openPatternDialog();
                break;
        }
    }

    private void openPatternDialog() {
            clickTimes++;
            if (PressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
                readPatternFromTechnicalSupport();
                clickTimes = 0;
            }
            PressedTime = System.currentTimeMillis();
    }

    public void readPatternFromTechnicalSupport() {
        patternPassword = "2103678";
        showExitPatternDialog();

        pattern_exitApp.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }
            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }
            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                final_pattern = PatternLockUtils.patternToString(pattern_exitApp, pattern);
                if (final_pattern.equals(patternPassword)) {
                    openSettings();
                } else {
                    pattern_exitApp.clearPattern();
                    exitPatternDialog.dismiss();
                }
            }
            @Override
            public void onCleared() {
            }
        });
    }

    private void showExitPatternDialog() {
            exitPatternDialog = new Dialog(this);
            exitPatternDialog.setContentView(R.layout.exit_pattern_dialog);
            pattern_exitApp = exitPatternDialog.findViewById(R.id.admin_verification_pattern);
            exitPatternDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exitPatternDialog.show();
            exitPatternDialog.setCancelable(false);
    }

    private void openSettings() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                final Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(intent);
            } else {
                final Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
            finish();
            System.exit(0);
    }

    private void openTaxiInformationScreen() {
        String userName = userName_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();
        AuthCredentials authCredentials = new AuthCredentials(userName, password);
        authCredentialsViewModel = ViewModelProviders.of((FragmentActivity) this).get(AuthCredentialsViewModel.class);
        authCredentialsViewModel.getToken(authCredentials,this, dialog).observe((LifecycleOwner) this, new Observer<List<AuthCredentialsError>>() {
            @Override
            public void onChanged(List<AuthCredentialsError> authCredentialsErrors) {
                for (int e = 0 ; e < authCredentialsErrors.size() ; e++ ){
                    if (authCredentialsErrors.get(e).getErrorNumber() == 1 || authCredentialsErrors.get(e).getErrorNumber() == 2){
                        showErrorMessage(authCredentialsErrors.get(e).getErrorNumber(),authCredentialsErrors.get(e).getErrorMessage(),true);
                    }
                }
            }
        });
    }

    private void showLoginView() {
            clickTimes++;
            if (PressedTime + 1000 > System.currentTimeMillis() && clickTimes >= 10) {
                openLoginLayout(true);
            }
            PressedTime = System.currentTimeMillis();
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
            String userName = userName_et.getText().toString().trim();
            String password = password_et.getText().toString().trim();
            app.setTechnicalSupportUserName(userName);
            app.setTechnicalSupportPassword(password);
            app.setNewLogin(true);
    }

}
