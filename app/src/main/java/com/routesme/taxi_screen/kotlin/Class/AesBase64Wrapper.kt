package com.routesme.taxi_screen.kotlin.Class

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Base64
import android.util.Log
import com.routesme.taxi_screen.kotlin.Model.EncryptModel
import java.io.UnsupportedEncodingException
import java.security.Key
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

class AesBase64Wrapper() {

    companion object {
        private  val encrypt: EncryptModel =  EncryptModel()
        private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }

    @SuppressLint("NewApi")
    fun encryptAndEncode(raw: String): String {
        return try {
            val cipher = getCipher(Cipher.ENCRYPT_MODE)
            Base64.encodeToString(cipher.doFinal(raw.toByteArray()), Base64.DEFAULT)
        } catch (t: Throwable) {
            throw RuntimeException(t)
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getBytes(str: String): ByteArray {
        return str.toByteArray(charset(encrypt.charsetName))
    }

    @Throws(Exception::class)
    private fun getCipher(mode: Int): Cipher {
        val c = Cipher.getInstance(encrypt.cipher)
        val iv = getBytes(encrypt.iv)
        c.init(mode, generateKey(), IvParameterSpec(iv))
        return c
    }

    @Throws(Exception::class)
    private fun generateKey(): Key {
        val factory = SecretKeyFactory.getInstance(encrypt.factory)
        val password = encrypt.password.toCharArray()
       // val salt = getBytes(encrypt.salt)
        val salt = getBytes(randomString)
        Log.d("Encryption", "RandomString: $randomString, Salt: $salt")
        val spec: KeySpec = PBEKeySpec(password, salt, encrypt.iterationCount, encrypt.keyLength)
        val tmp = factory.generateSecret(spec)
        val encoded = tmp.encoded
        return SecretKeySpec(encoded, encrypt.algorithm)
    }

    private val randomString = (1..15)
            .map { _ -> Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
}