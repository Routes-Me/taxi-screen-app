package com.example.routesapp.Model;

import android.app.Activity;

import com.example.routesapp.Class.Helper;

public class EncryptModel {

    private String secretKey, salt, cipher, factory, iterationCount, keyLength, algorithm , charsetName;

 //   private Activity activity;

    public EncryptModel(Activity activity) {
       // this.activity = activity;

        this.secretKey = Helper.getConfigValue(activity, "secretKey");
        this.salt = Helper.getConfigValue(activity, "salt");
        this.cipher = Helper.getConfigValue(activity, "cipher");
        this.factory = Helper.getConfigValue(activity, "factory");
        this.algorithm = Helper.getConfigValue(activity, "algorithm");
        this.charsetName = Helper.getConfigValue(activity, "charsetName");
        this.iterationCount = Helper.getConfigValue(activity, "iterationCount");
        this.keyLength = Helper.getConfigValue(activity, "keyLength");

    }


    public String getSecretKey() {
        return secretKey;
    }

    public String getSalt() {
        return salt;
    }

    public String getCipher() {
        return cipher;
    }

    public String getFactory() {
        return factory;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public int getIterationCount() {
        return Integer.parseInt(iterationCount);
    }

    public int getKeyLength() {
        return Integer.parseInt(keyLength);
    }
}
