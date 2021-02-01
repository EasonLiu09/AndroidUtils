package com.eason.mqtt

abstract class MCallbackAdapter : MCallback {
    override fun connectionLost(cause: Throwable?) {}
    override fun messageArrived(topic: String, message: String) {}
    override fun deliveryComplete(token: DeliveryToken) {}
    override fun pubMessage(token: DeliveryToken) {}
    override fun connectionFailure(cause: Throwable?) {}
    override fun unSubSuccess() {}
    override fun unSubFailure(cause: Throwable?) {}
    override fun connectionSucceed() {}
}