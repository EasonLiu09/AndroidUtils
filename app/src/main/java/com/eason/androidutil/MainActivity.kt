package com.eason.androidutil

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.eason.activity_ext.NavigationMode
import com.eason.activity_ext.getNavigationMode
import com.eason.log.logd
import com.eason.log.logv
import com.eason.mqtt.MCallbackAdapter
import com.eason.mqtt.MqttManager
import com.eason.mqtt.initMqtt
import com.eason.network.ConnectionStateManager
import com.eason.network.NetworkManager
import com.eason.permissionmanager.PermissionManager
import com.eason.preference.PreferenceManager
import com.eason.preference.PreferenceManager.putValue

class MainActivity : AppCompatActivity(),
    ConnectionStateManager.OnConnectionStateListener,
    PermissionManager.PermissionCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logd("is connected : ${NetworkManager.isInternetConnected()}")
        logd("is wifi connected: ${NetworkManager.isWiFiConnected()}")
        logv("is Cellular ${NetworkManager.isCellular()}")
        logd("navigation mode : ${getNavigationMode()}")


//        initMqtt { url = "tcp://210.61.46.162:2883" }

        findViewById<Button>(R.id.button).setOnClickListener {
            PermissionManager
                .with(this)
                .checkPermission(Manifest.permission.WRITE_CALENDAR, 1)
//            MqttManager.sub(
//                topic = "cc50e30002fd/eHouse/DoorSensor/Status",
//                callback = object : MCallbackAdapter() {
//                    override fun messageArrived(topic: String, message: String) {
//                        super.messageArrived(topic, message)
//                        Log.d("EASON", "message : $message")
//                    }
//                }
//            )
//            Log.d("EASON", "from preference : ${PreferenceManager.get("test", 1)}")

        }
//
//        logv("navigation mode : ${getNavigationMode()}")
//
//        PreferenceManager.init(this, "TEST")
//        PreferenceManager.edit {
//            putValue("test", 123)
//        }
    }

    override fun onResume() {
        super.onResume()
//        ConnectionStateManager.register(this, this)
    }

    override fun onPause() {
        super.onPause()
//        ConnectionStateManager.unregister(this, this)
    }

    override fun onAvailable() {

    }

    override fun onLost() {
    }

    override fun permissionsGranted(requestCode: Int, permissions: Array<String>) {
    }

    override fun permissionsDenied(requestCode: Int, permissions: Array<String>) {
    }

    override fun permissionsPermDenied(requestCode: Int, permissions: Array<String>) {
        Log.d("EASON", "permission perm denied.")
    }
}