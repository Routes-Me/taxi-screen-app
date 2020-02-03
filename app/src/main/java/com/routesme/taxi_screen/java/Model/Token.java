package com.routesme.taxi_screen.java.Model;

public class Token {

    private String access_token, token_type, expires_in, userName, issued, expires;


    //Constructor...
    public Token() {
    }

    public Token(String access_token, String token_type, String expires_in, String userName, String issued, String expires) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.userName = userName;
        this.issued = issued;
        this.expires = expires;
    }



    //Getter...
    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getUserName() {
        return userName;
    }

    public String getIssued() {
        return issued;
    }

    public String getExpires() {
        return expires;
    }



    //Setter...

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
