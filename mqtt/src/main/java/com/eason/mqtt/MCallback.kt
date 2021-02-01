package com.eason.mqtt

interface MCallback {
    public fun connectionLost(cause: Throwable?) {}
    public fun messageArrived(topic: String, message: String) {}
    public fun deliveryComplete(token: DeliveryToken) {}
    public fun pubMessage(token: DeliveryToken) {}
    public fun connectionFailure(cause: Throwable?) {}
    public fun unSubSuccess() {}
    public fun unSubFailure(cause: Throwable?) {}
    public fun connectionSucceed() {}
}