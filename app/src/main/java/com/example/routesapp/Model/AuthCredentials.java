package com.example.routesapp.Model;

import android.app.Activity;

import com.example.routesapp.Class.AesBase64Wrapper;

public class AuthCredentials {


    private String Username, Password;


    //Constructor....
    public AuthCredentials() {
    }


    public AuthCredentials( String username, String password) {



        this.Username = username;
        this.Password = password;
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
