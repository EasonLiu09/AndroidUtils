package com.eason.network

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build

object ConnectionStateManager {

    private var listeners: HashSet<OnConnectionStateListener>? = null
    private var connectivityManager: ConnectivityManager? = null

    private var isRegistered = false

    private var connectivityCallback: ConnectivityManager.NetworkCallback? = null
    private var connectivityReceiver: BroadcastReceiver? = null

    private var stickyIntent: Intent? = null

    fun register(context: Context, listener: OnConnectionStateListener) {

        if (connectivityManager == null) {
            connectivityManager =
                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        if (listeners == null) {
            listeners = HashSet()
        }

        listeners!!.add(listener)

        if (!isRegistered) {
            startListening(context)
            isRegistered = true
        }
    }

    fun unregister(context: Context, listener: OnConnectionStateListener) {
        listeners?.let {
            if (it.contains(listener)) it.remove(listener)

            if (it.isEmpty()) {
                release(context)
                isRegistered = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startListening(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectivityCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    listeners?.forEach { it.onAvailable() }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    listeners?.forEach { it.onLost() }
                }
            }

            connectivityManager?.registerDefaultNetworkCallback(connectivityCallback!!)

        } else {
            connectivityReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (stickyIntent != null) {
                        stickyIntent = null
                    }

                    intent?.let {
                        val networkInfo = it.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO) ?: null

                        if (networkInfo != null && networkInfo.isConnected) {
                            listeners?.forEach { listener -> listener.onAvailable() }
                        } else {
                            listeners?.forEach { listener -> listener.onLost() }
                        }
                    }
                }
            }

            stickyIntent = context.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    private fun release(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (connectivityCallback != null) {
                connectivityManager?.unregisterNetworkCallback(connectivityCallback!!)
            }
        } else {
            if (connectivityReceiver != null) {
                context.unregisterReceiver(connectivityReceiver!!)
            }
        }
        connectivityReceiver = null
        connectivityCallback = null
        connectivityManager = null
    }

    interface OnConnectionStateListener {
        fun onAvailable()
        fun onLost()
    }
}