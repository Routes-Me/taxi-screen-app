package com.routesme.taxi.Class

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import com.routesme.taxi.MVVM.Model.EncryptModel
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
        private  val encrypt: EncryptModel = EncryptModel()
        private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(encrypt.factory)

        val password = encrypt.password.toCharArray()
    }
/*
         Log.d("Encryption", "saltIndex: $saltIndexChars")
         Log.d("Encryption", "${saltIndexChars[0]}: ${saltIndexChars[0].toInt()}")
         Log.d("Encryption", "${saltIndexChars[1]}: ${saltIndexChars[1].toInt()}")
         Log.d("Encryption", "index: $saltIndexNumber")
         Log.d("Encryption", "saltBeginIndex: $saltBeginIndex")
         Log.d("Encryption", "saltExcluded: $saltExcluded")
         Log.d("Encryption", "fullSalt: $fullSalt")
         Log.d("Encryption", "realSalt: $realSalt")
*/

     fun getEncryptedString(str: String): String {
         val saltIndexChars = getSalt(2)
         var saltIndexNumber = 0
         saltIndexChars.toList().forEach { saltIndexNumber += it.toInt() }
         val saltBeginIndex = saltIndexNumber % 3

         val saltExcluded = getSalt(3)
         val fullSalt = getSalt(16)
         val realSalt = StringBuilder().append(fullSalt).toString().replace("""${saltExcluded.toList()}""".toRegex(), "")

         val encryptStr = encryptAndEncode(str, realSalt)


         val encryptedBody = StringBuilder()
         encryptedBody.append(encryptStr)

         val saltPart1 = fullSalt.substring(0,10)
         Log.d("Encryption", "saltPart1: $saltPart1")
         val saltPart2 = fullSalt.substring(10)
         Log.d("Encryption", "saltPart2: $saltPart2")

         encryptedBody.insert(saltBeginIndex,saltPart1)
         encryptedBody.insert(saltPart1.length + 1 + saltBeginIndex,saltPart2)

         val result = StringBuilder().append(saltIndexChars).append(saltExcluded).append(encryptedBody)

        /* Log.d("Encryption", "saltIndex: $saltIndexChars")
         Log.d("Encryption", "${saltIndexChars[0]}: ${saltIndexChars[0].toInt()}")
         Log.d("Encryption", "${saltIndexChars[1]}: ${saltIndexChars[1].toInt()}")
         Log.d("Encryption", "index: $saltIndexNumber")
         Log.d("Encryption", "saltBeginIndex: $saltBeginIndex")
         Log.d("Encryption", "saltExcluded: $saltExcluded")
         Log.d("Encryption", "fullSalt: $fullSalt")
         Log.d("Encryption", "realSalt: $realSalt")
         Log.d("Encryption", "encryptStr: $encryptStr")*/

         return result.toString()
    }

    @SuppressLint("NewApi")
    fun encryptAndEncode(str: String, salt: String): String {
        return try {
            val cipher = getCipher(salt)
            Base64.encodeToString(cipher.doFinal(str.toByteArray()), Base64.DEFAULT)
        } catch (t: Throwable) {
            throw RuntimeException(t)
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getBytes(str: String): ByteArray {
        return str.toByteArray(charset(encrypt.charsetName))
    }

    @Throws(Exception::class)
    private fun getCipher(salt: String): Cipher {
        val c = Cipher.getInstance(encrypt.cipher)
        val iv = getBytes(encrypt.iv)
        c.init(Cipher.ENCRYPT_MODE, generateKey(salt), IvParameterSpec(iv))
        return c
    }

    @Throws(Exception::class)
    private fun generateKey(salt: String): Key {
       // val salt = getBytes(encrypt.salt)
       // Log.d("Encryption", "RandomString: $randomString, Salt: $salt")
        val spec: KeySpec = PBEKeySpec(password, getBytes(salt), encrypt.iterationCount, encrypt.keyLength)
        val tmp = factory.generateSecret(spec)
        val encoded = tmp.encoded
        return SecretKeySpec(encoded, encrypt.algorithm)
    }

    private fun getSalt(charsNumber: Int): String = (1..charsNumber)
            .map { _ -> Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
}