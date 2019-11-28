package com.example.routesapp.View.Login.LoginFragments;

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

import com.example.routesapp.Class.AesBase64Wrapper;
import com.example.routesapp.Interface.RoutesApi;
import com.example.routesapp.Model.AuthCredentials;
import com.example.routesapp.Model.AuthCredentialsError;
import com.example.routesapp.Model.AuthCredentialsViewModel;
import com.example.routesapp.Model.Token;
import com.example.routesapp.R;
import com.example.routesapp.View.Login.Activity.LearnMoreScreen;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalLoginFragment extends Fragment implements View.OnClickListener {


    private static  String originalString = "Abdullah Soubeih";

    private AuthCredentialsViewModel authCredentialsViewModel;

    private View nMainView;

    private TextView btn_next;
    private LinearLayout btn_learnMore;

    private EditText userName_et;
    private ShowHidePasswordEditText password_et;
    private TextView userName_error_tv, password_error_tv;

  //  private AesBase64Wrapper aesBase64Wrapper;

    public TechnicalLoginFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
        nMainView = inflater.inflate(R.layout.technical_login_fragment, container, false);

        initialize();

       // encryptUserName();


        return nMainView;


    }

    private void encryptUserName() {
        //Using AES 256 To Encryption & Decryption ...
        //Toast.makeText(getActivity(), "encrypt:   "+ aes.encrypt(getActivity(),originalString)    +  "   ,decrypt:  " + aes.decrypt(getActivity(),aes.encrypt(getActivity(),originalString)), Toast.LENGTH_SHORT).show();
        //  email_et.setText(AES.encrypt(getActivity(),originalString));

        AesBase64Wrapper aesBase64Wrapper = new AesBase64Wrapper(getActivity());

        try {
            Toast.makeText(getActivity(), "Encrypt:  " + aesBase64Wrapper.encryptAndEncode(originalString) + "   , Decrypt:  " + aesBase64Wrapper.decodeAndDecrypt(aesBase64Wrapper.encryptAndEncode(originalString)) +"   , Original is:  " + originalString , Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // email_et.setText(aesBase64Wrapper.encryptAndEncode(originalString));

    }

    private void initialize() {




        btn_next = nMainView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_learnMore = nMainView.findViewById(R.id.btn_learnMore);
        btn_learnMore.setOnClickListener(this);

        userName_et = nMainView.findViewById(R.id.email_et);
        userName_error_tv = nMainView.findViewById(R.id.userName_error_tv);
        password_et = nMainView.findViewById(R.id.password_et);
        password_error_tv = nMainView.findViewById(R.id.password_error_tv);
        editTextListener();

     //   aesBase64Wrapper = new AesBase64Wrapper(getActivity());

    }

    private void editTextListener() {
        userName_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showErrorMessage(1,"",false);
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


       String userName = userName_et.getText().toString().trim();
       String password = password_et.getText().toString().trim();
/*
        if (userName.isEmpty()){
            showErrorMessage(userName_et, userName_error_tv,"* userName Required",true);
            return;
        }
        if (password.isEmpty()){
            showErrorMessage(password_et, password_error_tv,"* Password Required",true);
            return;
        }
        if (password.length() < 8){
            showErrorMessage(password_et, password_error_tv,"* Minimum Password is 8 digit",true);
            return;
        }
*/
        /*
        authCredentialsViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(AuthCredentialsViewModel.class);
        authCredentialsViewModel.getToken(new AuthCredentials(aesBase64Wrapper.encryptAndEncode(email),aesBase64Wrapper.encryptAndEncode(password)),getActivity()).observe((LifecycleOwner) getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getActivity(), "response:  " + s, Toast.LENGTH_SHORT).show();
            }
        });
*/

        AuthCredentials authCredentials = new AuthCredentials(userName, password);
        authCredentialsViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(AuthCredentialsViewModel.class);
        authCredentialsViewModel.getToken(authCredentials,getActivity()).observe((LifecycleOwner) getActivity(), new Observer<List<AuthCredentialsError>>() {
                    @Override
                    public void onChanged(List<AuthCredentialsError> authCredentialsErrors) {
                        for (int e = 0 ; e < authCredentialsErrors.size() ; e++ ){
                          //  Toast.makeText(getActivity(), "no. of errors:   " + authCredentialsErrors.size(), Toast.LENGTH_SHORT).show();
                            showErrorMessage(authCredentialsErrors.get(e).getErrorNumber(),authCredentialsErrors.get(e).getErrorMasseg(),true);

                        }
                    }
                });


                //  authCredentials.setUsername("Tevu5gmGAJFFmcO9dMdYWw==");
                //   authCredentials.setPassword("kjn3aW+SqtA3lPiErEyzyQ==");
/*
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RoutesApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RoutesApi api = retrofit.create(RoutesApi.class);
        Call<Token> call = api.loginUser(authCredentials);


        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

              //  String token = null;


             //  Token token = new Gson().fromJson(response.toString(),Token.class);

                if (response.code() == 401) {
                    // launch login activity using `this.context`
                    Toast.makeText(getActivity(), "error code:  " + 401, Toast.LENGTH_SHORT).show();
                } else {
                   // onSuccess(response.body());
                    try {
                        Toast.makeText(getActivity(), "tt:   " +  response.body().getAccess_token(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }




            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(getActivity(), "error:   " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */



   //  getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.login_fragment_container, new TabletDataFragment()).commit();
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
            textView.setText(errorStr);
            textView.setVisibility(View.VISIBLE);
            return;
        }else {
            editText.setBackgroundResource(R.drawable.grey_border);
            textView.setVisibility(View.INVISIBLE);
        }



    }










}
