package com.routesme.taxi_screen.Server.Class;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.routesme.taxi_screen.Model.EncryptModel;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;



public class AesBase64Wrapper {

   // private static String IV = "IV_VALUE_16_BYTE";
   // private static String PASSWORD = "PASSWORD_VALUE";
   // private static String SALT = "SALT_VALUE";

    private static EncryptModel encrypt;

    public AesBase64Wrapper(Activity activity) {
        this.encrypt = new EncryptModel(activity);
    }


    @SuppressLint("NewApi")
    public String encryptAndEncode(String raw) {
        try {
            Cipher c = getCipher(Cipher.ENCRYPT_MODE);
           // byte[] encryptedVal = c.doFinal(getBytes(raw));
           // String s = getString(Base64.encodeBase64(encryptedVal));
            //return s;
            return java.util.Base64.getEncoder().encodeToString(c.doFinal(raw.getBytes()));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @SuppressLint("NewApi")
    public String decodeAndDecrypt(String encrypted) throws Exception {
       // byte[] decodedValue = Base64.decodeBase64(getBytes(encrypted));
        Cipher c = getCipher(Cipher.DECRYPT_MODE);
       // byte[] decValue = c.doFinal(decodedValue);
        return new String(c.doFinal(java.util.Base64.getDecoder().decode(encrypted)));
       // return new String(decValue);
    }

    private String getString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, encrypt.getCharsetName());
    }

    private byte[] getBytes(String str) throws UnsupportedEncodingException {
        return str.getBytes(encrypt.getCharsetName());
    }

    private Cipher getCipher(int mode) throws Exception { Cipher c = Cipher.getInstance(encrypt.getCipher());
        byte[] iv = getBytes(encrypt.getIv());
        c.init(mode, generateKey(), new IvParameterSpec(iv));
        return c;
    }

    private Key generateKey() throws Exception {
       // SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKeyFactory factory = SecretKeyFactory.getInstance(encrypt.getFactory());
        char[] password = encrypt.getPassword().toCharArray();
        byte[] salt = getBytes(encrypt.getSalt());

        KeySpec spec = new PBEKeySpec(password, salt, encrypt.getIterationCount(), encrypt.getKeyLength());
        SecretKey tmp = factory.generateSecret(spec);
        byte[] encoded = tmp.getEncoded();
        return new SecretKeySpec(encoded, encrypt.getAlgorithm());
    }
}