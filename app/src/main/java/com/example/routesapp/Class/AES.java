package com.example.routesapp.Class;

import android.app.Activity;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.routesapp.Model.EncryptModel;

import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

   // private static String secretKey = "boooooooooom!!!!";
  //  private static String salt = "ssshhhhhhhhhhh!!!!";


    private static EncryptModel encryptModel;

    public AES(Activity activity) {
        this.encryptModel = new EncryptModel(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(Activity activity, String strToEncrypt) {
        try {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(encryptModel.getFactory());
            KeySpec spec = new PBEKeySpec(encryptModel.getSecretKey().toCharArray(), encryptModel.getSalt().getBytes(), encryptModel.getIterationCount(), encryptModel.getKeyLength());
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), encryptModel.getAlgorithm());

            Cipher cipher = Cipher.getInstance(encryptModel.getCipher());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(encryptModel.getCharsetName())));
        }
        catch (Exception e)
        {
           // System.out.println("Error while encrypting: " + e.toString());
          //  Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(Activity activity, String strToDecrypt) {
        try {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(encryptModel.getFactory());
            KeySpec spec = new PBEKeySpec(encryptModel.getSecretKey().toCharArray(), encryptModel.getSalt().getBytes(), encryptModel.getIterationCount(), encryptModel.getKeyLength());
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), encryptModel.getAlgorithm());

            Cipher cipher = Cipher.getInstance(encryptModel.getCipher());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
          //  System.out.println("Error while decrypting: " + e.toString());
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }


}
