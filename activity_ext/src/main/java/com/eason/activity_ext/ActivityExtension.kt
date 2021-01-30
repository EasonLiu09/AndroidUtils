package com.eason.activity_ext

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

enum class NavigationMode(val value: Int) {
    THREE_BUTTONS(0),
    TWO_BUTTONS(1),
    GESTURE(2),
}

inline fun <reified T : AppCompatActivity> AppCompatActivity.launchActivity(
    options: Bundle? = null,
    crossinline block: Intent.() -> Unit = {}
) {

    val intent = newIntent<T>(this)
    intent.block()
    startActivity(intent, options)
}

inline fun <reified T : AppCompatActivity> newIntent(context: Context): Intent {
    return Intent(context, T::class.java)
}

fun Context.systemNavigationMode(): NavigationMode {
    return with(
        resources.getIdentifier(
            "config_navBarInteractionMode",
            "integer",
            "android"
        )
    ) {
        NavigationMode.values().find { mode ->
            mode.value == if (0 < this) resources.getInteger(this) else 0
        } ?: NavigationMode.THREE_BUTTONS
    }
}

inline fun AppCompatActivity.setUpToolbar(
    toolbar: MaterialToolbar,
    title: String = "",
    showNavigation: Boolean = true,
    crossinline navigationCallback: () -> Unit = {}
) {

    setSupportActionBar(toolbar)
    if (title != "") {
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = title
    } else {
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    if (showNavigation) {
        toolbar.setNavigationOnClickListener {
            navigationCallback()
        }
    }

}