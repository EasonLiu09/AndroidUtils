package com.eason.preference

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object PreferenceManager {

    @PublishedApi
    internal lateinit var preference: SharedPreferences
        private set

    fun init(context: Context, name: String = "Data Preference") {
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .build()

        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyGenParameterSpec(spec)
            .build()

        preference = EncryptedSharedPreferences.create(
            context.applicationContext,
            name,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    inline fun edit(isApply: Boolean = true, block: SharedPreferences.Editor.() -> Unit) {
        val editor = preference.edit()
        block(editor)
        if (isApply)
            editor.apply()
        else
            editor.commit()
    }


    inline fun <reified T> SharedPreferences.Editor.putValue(key: String, content: T) {
        when (T::class.simpleName) {
            Int::class.simpleName -> { putInt(key, content as Int) }
            String::class.simpleName -> { putString(key, content as String) }
            Long::class.simpleName -> { putLong(key, content as Long) }
            Float::class.simpleName -> { putFloat(key, content as Float) }
            Boolean::class.simpleName -> { putBoolean(key, content as Boolean) }
        }
    }

    inline fun <reified T> get(key: String, defaultValue: T): T {
        return when (T::class.simpleName) {
            Int::class.simpleName -> preference.getInt(key, defaultValue as Int) as T
            String::class.simpleName -> preference.getString(key, defaultValue as String) as T
            Long::class.simpleName -> preference.getLong(key, defaultValue as Long) as T
            Float::class.simpleName -> preference.getFloat(key, defaultValue as Float) as T
            Boolean::class.simpleName -> preference.getBoolean(key, defaultValue as Boolean) as T
            else -> throw IllegalArgumentException("No type matched")
        }
    }

}