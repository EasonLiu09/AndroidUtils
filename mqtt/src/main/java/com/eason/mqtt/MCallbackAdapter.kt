package com.eason.mqtt

abstract class MCallbackAdapter {
    fun connectionLost(cause: Throwable?) {}
    fun messageArrived(topic: String, message: String) {}
    fun deliveryComplete(token: DeliveryToken) {}
    fun pubMessage(token: DeliveryToken) {}
    fun connectionFailure(cause: Throwable?) {}
    fun unSubSuccess() {}
    fun unSubFailure(cause: Throwable?) {}
    fun connectionSucceed() {}
}