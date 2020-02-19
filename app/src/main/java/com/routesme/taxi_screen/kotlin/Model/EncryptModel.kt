package com.routesme.taxi_screen.kotlin.Model

import android.content.Context
import com.routesme.taxi_screen.kotlin.Class.Helper

class EncryptModel(context: Context) {
    private val secretKey: String = Helper.getConfigValue(context, "secretKey").toString()
    val iv: String = Helper.getConfigValue(context, "iv").toString()
    val password: String = Helper.getConfigValue(context, "password").toString()
    val salt: String = Helper.getConfigValue(context, "salt").toString()
    val cipher: String = Helper.getConfigValue(context, "cipher").toString()
    val factory: String = Helper.getConfigValue(context, "factory").toString()
    val iterationCount: Int = Helper.getConfigValue(context, "iterationCount")!!.toInt()
    val keyLength: Int = Helper.getConfigValue(context, "keyLength")!!.toInt()
    val algorithm: String = Helper.getConfigValue(context, "algorithm").toString()
    val charsetName: String = Helper.getConfigValue(context, "charsetName").toString()
}