package com.eason.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager

object NetworkManager {

    private lateinit var wifiManager: WifiManager
    private lateinit var connectivityManager: ConnectivityManager

    fun init(context: Context) {
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @SuppressLint("MissingPermission")
    public fun isWiFiConnected(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork!!)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))

        } else {

            if (connectivityManager.activeNetworkInfo != null) {
                connectivityManager.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI
            } else {
                false
            }
        }
    }

    @SuppressLint("MissingPermission")
    public fun isInternetConnected(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }


    public fun getWiFiSSID(): String {
        return if (isInternetConnected()) {
            val wifiInfo = wifiManager.connectionInfo
            val len = wifiInfo.ssid.length

            if (wifiInfo.ssid.startsWith("\"") && wifiInfo.ssid.endsWith("\"")) {
                wifiInfo.ssid.substring(1, len - 1)
            } else {
                wifiInfo.ssid
            }
        } else {
            ""
        }
    }

    public fun getBssid(): String {
        return if (isInternetConnected()) {
            wifiManager.connectionInfo.bssid
        } else {
            ""
        }
    }

    @SuppressLint("MissingPermission")
    public fun isCellular(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork!!)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isAvailable && networkInfo.subtype == TelephonyManager.NETWORK_TYPE_LTE
        }
    }

    public fun isWiFiEnable() = wifiManager.isWifiEnabled
    public fun is5GSupported() = wifiManager.is5GHzBandSupported


}