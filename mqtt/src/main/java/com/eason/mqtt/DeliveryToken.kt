package com.eason.mqtt

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken

data class DeliveryToken(
    private val result: IMqttDeliveryToken?
) {

    fun waitForCompletion() {
        result?.waitForCompletion()
    }

    fun waitForCompletion(timeout: Long) {
        result?.waitForCompletion(timeout)
    }

    fun isCompleted() = result?.isComplete
}
