package com.example.routesapp.Model;

public class AuthCredentials {

    private String Username, Password;


    //Constructor....
    public AuthCredentials() {
    }


    public AuthCredentials(String username, String password) {
        Username = username;
        Password = password;
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
