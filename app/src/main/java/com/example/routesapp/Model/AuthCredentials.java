package com.example.routesapp.Model;

import android.app.Activity;

import com.example.routesapp.Class.AesBase64Wrapper;

public class AuthCredentials {

    private AesBase64Wrapper aesBase64Wrapper;

    private String Username, Password;


    //Constructor....
    public AuthCredentials() {
    }


    public AuthCredentials(Activity activity, String username, String password) {

        aesBase64Wrapper = new AesBase64Wrapper(activity);

        Username = aesBase64Wrapper.encryptAndEncode(username);
        Password = aesBase64Wrapper.encryptAndEncode(password);
    }




    //Getter....
    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }





    //Setter....
    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
