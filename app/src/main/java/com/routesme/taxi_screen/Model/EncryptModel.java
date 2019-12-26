package com.routesme.taxi_screen.Model;

import android.app.Activity;

import com.routesme.taxi_screen.Class.Helper;

public class EncryptModel {

    private String secretKey, iv, password, salt, cipher, factory, iterationCount, keyLength, algorithm , charsetName;

 //   private Activity activity;

    public EncryptModel(Activity activity) {
       // this.activity = activity;

        this.secretKey = Helper.getConfigValue(activity, "secretKey");
        this.iv = Helper.getConfigValue(activity, "iv");
        this.password = Helper.getConfigValue(activity, "password");
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

    public String getIv() {
        return iv;
    }

    public String getPassword() {
        return password;
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
