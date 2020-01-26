//package com.routesme.taxi_screen.View.Login
//
//import javax.crypto.ShortBufferException
//import java.security.NoSuchAlgorithmException
//import javax.crypto.NoSuchPaddingException
//import javax.crypto.BadPaddingException
//import javax.crypto.IllegalBlockSizeException
//import java.io.UnsupportedEncodingException
//import javax.crypto.Cipher
//import javax.crypto.spec.SecretKeySpec
//
//import java.security.InvalidKeyException
//import java.security.Security
//
//class AESEncryptor {
//
//    fun String.encrypt(password: String): String {
//        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
//        val iv = ByteArray(16)
//        val charArray = password.toCharArray()
//        for (i in 0 until charArray.size){
//            iv[i] = charArray[i].toByte()
//        }
//        val ivParameterSpec = IvParameterSpec(iv)
//
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
//
//        val encryptedValue = cipher.doFinal(this.toByteArray())
//        return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
//    }
//
//    fun String.decrypt(password: String): String {
//        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
//        val iv = ByteArray(16)
//        val charArray = password.toCharArray()
//        for (i in 0 until charArray.size){
//            iv[i] = charArray[i].toByte()
//        }
//        val ivParameterSpec = IvParameterSpec(iv)
//
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
//
//        val decryptedByteValue = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
//        return String(decryptedByteValue)
//    }
//}