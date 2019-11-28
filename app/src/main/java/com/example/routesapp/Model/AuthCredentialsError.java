package com.example.routesapp.Model;

public class AuthCredentialsError {

    private int ErrorNumber ;
    private String ErrorMasseg ;


    //Constructor...
    public AuthCredentialsError() {
    }

    public AuthCredentialsError(int errorNumber, String errorMasseg) {
        ErrorNumber = errorNumber;
        ErrorMasseg = errorMasseg;
    }

    //Getter...
    public int getErrorNumber() {
        return ErrorNumber;
    }

    public String getErrorMasseg() {
        return ErrorMasseg;
    }



    //Setter...

    public void setErrorNumber(int errorNumber) {
        ErrorNumber = errorNumber;
    }

    public void setErrorMasseg(String errorMasseg) {
        ErrorMasseg = errorMasseg;
    }
}
