package com.eason.network

import android.content.Context
import androidx.startup.Initializer

class NetworkInitializer : Initializer<NetworkManager> {

    override fun create(context: Context): NetworkManager {
        NetworkManager.init(context.applicationContext)
        return NetworkManager
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}