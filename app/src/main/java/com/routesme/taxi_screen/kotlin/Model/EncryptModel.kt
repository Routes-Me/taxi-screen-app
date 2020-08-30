package com.routesme.taxi_screen.kotlin.Model

import com.routesme.taxi_screen.kotlin.Class.Helper
import com.routesme.taxiscreen.R

class EncryptModel() {
    val iv: String = Helper.getConfigValue("iv", R.raw.encryption).toString()
    val password: String = Helper.getConfigValue("password", R.raw.encryption).toString()
    val cipher: String = Helper.getConfigValue("cipher", R.raw.encryption).toString()
    val factory: String = Helper.getConfigValue("factory", R.raw.encryption)!!
    val iterationCount: Int = Helper.getConfigValue("iterationCount", R.raw.encryption)!!.toInt()
    val keyLength: Int = Helper.getConfigValue("keyLength", R.raw.encryption)!!.toInt()
    val algorithm: String = Helper.getConfigValue("algorithm", R.raw.encryption).toString()
    val charsetName: String = Helper.getConfigValue("charsetName", R.raw.encryption).toString()
}