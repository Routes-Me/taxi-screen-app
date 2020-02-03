package com.routesme.taxi_screen.java.Server.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Base64;
import com.routesme.taxi_screen.java.Model.EncryptModel;
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

    private static EncryptModel encrypt;

    public AesBase64Wrapper(Activity activity) {
        this.encrypt = new EncryptModel(activity);
    }

    @SuppressLint("NewApi")
    public String encryptAndEncode(String raw) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            return Base64.encodeToString(cipher.doFinal(raw.getBytes()), Base64.DEFAULT);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
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
        SecretKeyFactory factory = SecretKeyFactory.getInstance(encrypt.getFactory());
        char[] password = encrypt.getPassword().toCharArray();
        byte[] salt = getBytes(encrypt.getSalt());

        KeySpec spec = new PBEKeySpec(password, salt, encrypt.getIterationCount(), encrypt.getKeyLength());
        SecretKey tmp = factory.generateSecret(spec);
        byte[] encoded = tmp.getEncoded();
        return new SecretKeySpec(encoded, encrypt.getAlgorithm());
    }
}