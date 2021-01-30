package com.eason.log

import android.util.Log
import java.util.regex.Pattern

object LogManager {

    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    private const val MAX_TAG_LENGTH = 23

    internal var isDebugMode = true

    fun isDebuggable(enable: Boolean) {
        isDebugMode = enable
    }

    internal fun findTag(): String {
        var tag = Throwable().stackTrace[1].className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        if (tag.length > MAX_TAG_LENGTH) {
            tag = tag.substring(0, MAX_TAG_LENGTH)
        }

        return tag
    }
}

fun Any.logd(message: String) {
    if (LogManager.isDebugMode) {
        Log.d(LogManager.findTag(), message)
    }
}

fun Any.loge(message: String) {
    if (LogManager.isDebugMode) {
        Log.e(LogManager.findTag(), message)
    }
}

fun Any.logi(message: String) {
    if (LogManager.isDebugMode) {
        Log.i(LogManager.findTag(), message)
    }
}

fun Any.logv(message: String) {
    if (LogManager.isDebugMode) {
        Log.v(LogManager.findTag(), message)
    }
}