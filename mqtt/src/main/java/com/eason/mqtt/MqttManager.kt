package com.eason.mqtt

import android.util.Log
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import kotlin.random.Random

object MqttManager {

    private const val TAG = "MqttManager"

    private lateinit var client: MqttAsyncClient
    private lateinit var serverUrl: String
    private lateinit var id: String
    private lateinit var name: String
    private var pwd: CharArray? = null

    private var isDebugMode = false

    // Call client.connect while client is connecting will throws an error.
    @Volatile
    private var isConnecting = false

    internal fun initClient() {
        client = MqttAsyncClient(serverUrl, id, MemoryPersistence())
    }

    fun setDebugMode(enable: Boolean) {
        isDebugMode = enable
    }

    fun sub(topic: String, qos: Int = 1, callback: MCallbackAdapter) {
        sub(arrayOf(topic), qos, callback)
    }

    fun sub(topics: Array<String>, qos: Int, callback: MCallbackAdapter) {

        setUpCallback(callback)

        val bunchOfQos = IntArray(topics.size) { qos }

        if (client.isConnected) {
            client.subscribe(topics, bunchOfQos)
        } else {
            tryConnect(callback) {
                client.subscribe(topics, bunchOfQos)
            }
        }
    }

    fun unSub(topic: String, callback: MCallbackAdapter? = null) {
        unSub(arrayOf(topic), callback)
    }

    fun unSub(topics: Array<String>, callback: MCallbackAdapter? = null) {
        if (client.isConnected) {
            client.unsubscribe(topics, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    callback?.unSubSuccess()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    callback?.unSubFailure(exception)
                }
            })
        } else {
            tryConnect(callback ?: object : MCallbackAdapter() {}) {
                client.unsubscribe(topics, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        callback?.unSubSuccess()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        callback?.unSubFailure(exception)
                    }
                })
            }
        }
    }

    fun pub(topic: String, message: String, q: Int = 1, retain: Boolean = true, callback: MCallbackAdapter?) {

        val mqttMessage = MqttMessage(message.toByteArray()).apply {
            qos = q
            isRetained = retain
        }

        if (client.isConnected) {
            val token = DeliveryToken(client.publish(topic, mqttMessage))
            callback?.pubMessage(token)
        } else {
            tryConnect(callback ?: object : MCallbackAdapter() {}) {
                val token = DeliveryToken(client.publish(topic, mqttMessage))
                callback?.pubMessage(token)
            }
        }
    }

    private fun setUpCallback(callback: MCallbackAdapter) {
        client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                callback.connectionLost(cause)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (topic == null) return
                callback.messageArrived(topic, message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                callback.deliveryComplete(DeliveryToken(token))
            }
        })
    }

    private inline fun tryConnect(callback: MCallbackAdapter, crossinline onSuccessAction: () -> Unit) {

        if (isConnecting) return
        isConnecting = true

        client.connect(getConnectionOptions(), object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                if (isDebugMode) Log.d(TAG, "Sub connection succeed")
                isConnecting = false
                onSuccessAction()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                if (isDebugMode) Log.d(TAG, "Sub connection succeed")
                callback.connectionFailure(exception)
                isConnecting = false
            }
        })
    }

    /**
     *  Disconnect for no reason and cannot reconnect, must use another client id
     *
     */
    fun refreshClient(u: String = "", id: String = MqttAsyncClient.generateClientId() + getRandomNumber()) {

        if (u != "") serverUrl = u

        if (client.isConnected) {
            isConnecting = false
            client.disconnectForcibly()
            client.close(true)
        }

        client = MqttAsyncClient(serverUrl, id, MemoryPersistence())
    }

    private fun getConnectionOptions() = MqttConnectOptions().apply {
        isAutomaticReconnect = true
        connectionTimeout = 10
        userName = name
        if (pwd != null) {
            password = pwd
        }
    }

    private fun getRandomNumber() = Random.nextInt(50)

    @DslMarker
    annotation class BuilderDsl

    class Builder {
        var url = ""
        var clientId: String = MqttAsyncClient.generateClientId()
        var userName = "unknown"
        var password: CharArray? = null

        internal fun build() {
            require(!(url.isEmpty() || url.isBlank())) { "URL must not be null or empty" }

            serverUrl = url
            id = clientId
            name = userName
            pwd = password
            initClient()
        }
    }
}

@MqttManager.BuilderDsl
fun initMqtt(initialize: MqttManager.Builder.() -> Unit) {
    MqttManager.Builder().apply(initialize).build()
}