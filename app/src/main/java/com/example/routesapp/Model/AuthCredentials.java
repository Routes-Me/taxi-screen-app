package com.example.routesapp.Model;

public class AuthCredentials {

    private String username, password;


    //Constructor....
    public AuthCredentials() {
    }

    public AuthCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }



    //Getter....
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    //Setter....
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
