package com.eason.androidutil

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.eason.log.*
import com.eason.mqtt.MqttManager
import com.eason.mqtt.initMqtt
import com.eason.network.ConnectionStateManager
import com.eason.network.NetworkManager
import com.eason.permissionmanager.PermissionManager
import com.eason.preference.PreferenceManager

class MainActivity : AppCompatActivity(),
    ConnectionStateManager.OnConnectionStateListener,
    PermissionManager.PermissionCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        logd(NetworkManager.getBssid())

        PermissionManager
            .with(this)
            .checkPermission(Manifest.permission.WRITE_CALENDAR, 1)

        PreferenceManager.init(this)
    }

    override fun onResume() {
        super.onResume()
        ConnectionStateManager.register(this, this)
    }

    override fun onPause() {
        super.onPause()
        ConnectionStateManager.unregister(this, this)
    }

    override fun onAvailable() {
        logd("onAvailable")
    }

    override fun onLost() {
        logd("onLost")
    }

    override fun permissionsGranted(requestCode: Int, permissions: Array<String>) {
        logv("permission granted.")
    }

    override fun permissionsDenied(requestCode: Int, permissions: Array<String>) {
        loge("permission denied.")
    }

    override fun permissionsPermDenied(requestCode: Int, permissions: Array<String>) {
        logi("permission perm denied.")
    }
}