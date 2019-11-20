package com.example.routesapp.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class CurrenciesModel {

    private String Currency_Code, Currency_Name_En, Currency_Name_Ar, Currency_Name_Or, Currency_Name_Ta;
    private float Currency_Eexchange_Rate;


    private Context context;
    //sharedPreference Storage
    private SharedPreferences sharedPreferences;
    private String savedLanguage = null;

    //Constructor ...
    public CurrenciesModel() {


    }

    public CurrenciesModel(String currency_Code, String currency_Name_En, String currency_Name_Ar, String currency_Name_Or, String currency_Name_Ta, float currency_Eexchange_Rate) {
        this.Currency_Code = currency_Code;
        this.Currency_Eexchange_Rate = currency_Eexchange_Rate;


        this.Currency_Name_En = currency_Name_En;
        this.Currency_Name_Ar = currency_Name_Ar;
        this.Currency_Name_Or = currency_Name_Or;
        this.Currency_Name_Ta = currency_Name_Ta;

    }



    //Getter...

    public String getCurrency_Code() {
        return Currency_Code;
    }

    public float getCurrency_Eexchange_Rate() {
        return Currency_Eexchange_Rate;
    }


    public String getCurrency_Name(Context context) {

        String Currency_Name;

       // this.context = context;

        //sharedPreference Storage
        sharedPreferences = context.getSharedPreferences("userData", Activity.MODE_PRIVATE);
        savedLanguage = sharedPreferences.getString("Language", "English");



        switch (savedLanguage){

            case "English":
                Currency_Name = Currency_Name_En;
                break;

            case "Arabic":
                String name_Ar = "" + Currency_Name_Ar;

                if (!name_Ar.trim().isEmpty() && !name_Ar.trim().equals("null")){
                    Currency_Name = name_Ar;
                }else {
                    Currency_Name = Currency_Name_En;
                }

                break;

            case "Urdu":

                String name_Or = "" + Currency_Name_Or;

                if (!name_Or.trim().isEmpty() && !name_Or.trim().equals("null")){
                    Currency_Name = name_Or;
                }else {
                    Currency_Name = Currency_Name_En;
                }

                break;

            case "Tagalog":

                String name_Ta = "" + Currency_Name_Ta;

                if (!name_Ta.trim().isEmpty() && !name_Ta.trim().equals("null")){
                    Currency_Name = name_Ta;
                }else {
                    Currency_Name = Currency_Name_En;
                }

                break;

            default:
                Currency_Name = Currency_Name_En;
                break;

        }

        return Currency_Name;

    }



    //Setter...

    public void setCurrency_Code(String currency_Code) {
        Currency_Code = currency_Code;
    }

    public void setCurrency_Name_En(String currency_Name_En) {
        Currency_Name_En = currency_Name_En;
    }

    public void setCurrency_Name_Ar(String currency_Name_Ar) {
        Currency_Name_Ar = currency_Name_Ar;
    }

    public void setCurrency_Name_Or(String currency_Name_Or) {
        Currency_Name_Or = currency_Name_Or;
    }

    public void setCurrency_Name_Ta(String currency_Name_Ta) {
        Currency_Name_Ta = currency_Name_Ta;
    }

    public void setCurrency_Eexchange_Rate(float currency_Eexchange_Rate) {
        Currency_Eexchange_Rate = currency_Eexchange_Rate;
    }
}
