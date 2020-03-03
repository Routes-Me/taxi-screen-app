package com.routesme.taxi_screen.kotlin.Model

import android.content.Context
import com.routesme.taxi_screen.kotlin.Class.App
import com.routesme.taxi_screen.kotlin.Class.Helper

class EncryptModel() {
    private val secretKey: String = Helper.getConfigValue("secretKey").toString()
    val iv: String = Helper.getConfigValue("iv").toString()
    val password: String = Helper.getConfigValue("password").toString()
    val salt: String = Helper.getConfigValue("salt").toString()
    val cipher: String = Helper.getConfigValue("cipher").toString()
    val factory: String = Helper.getConfigValue("factory").toString()
    val iterationCount: Int = Helper.getConfigValue("iterationCount")!!.toInt()
    val keyLength: Int = Helper.getConfigValue("keyLength")!!.toInt()
    val algorithm: String = Helper.getConfigValue("algorithm").toString()
    val charsetName: String = Helper.getConfigValue("charsetName").toString()
}