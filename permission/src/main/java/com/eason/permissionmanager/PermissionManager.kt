package com.eason.permissionmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

object PermissionManager {

    private const val TAG = "PermissionManager"

    internal const val REQUEST_CODE_KEY = "com.eason.permission_manager.request_code_key"
    internal const val REQUEST_PERMISSIONS = "com.eason.permission_manager.request_permissions"
    internal const val REQUEST_PERM_DENIED_PERMISSIONS =
        "com.eason.permission_manager.perm_denied_permissions"

    private lateinit var askActivity: WeakReference<AppCompatActivity>

    private const val PERMISSION_MANAGER_PREFERENCE_NAME =
        "com.eason.permission_manager.preference_name"
    private const val PERMISSION_FIRST_TIME_CHECK = "com.eason.permission_manager.first_time_check"

    private var checkPermissionFirstTime = true
    private var preference: SharedPreferences? = null

    private lateinit var callback: WeakReference<PermissionCallback>


    fun with(activity: AppCompatActivity): Helper {

        if (activity !is PermissionCallback) {
            throw IllegalStateException("Needs to implementing PermissionCallback to receive user's response.")
        }

        askActivity = WeakReference(activity)
        callback = WeakReference(activity)
        preference =
            activity.getSharedPreferences(PERMISSION_MANAGER_PREFERENCE_NAME, Context.MODE_PRIVATE)
        checkPermissionFirstTime =
            preference!!.getBoolean(PERMISSION_FIRST_TIME_CHECK, true)

        return Helper()
    }

    class Helper {

        fun checkPermission(permission: String, requestCode: Int) {
            checkPermissions(arrayOf(permission), requestCode)
        }

        fun checkPermissions(permissions: Array<String>, requestCode: Int) {
            val denied = ArrayList<String>(permissions.size)
            val permanentlyDenied = ArrayList<String>(permissions.size)
            val granted = ArrayList<String>(permissions.size)

            for (permission in permissions) {

                Log.d(TAG, "size : ${permissions.size}, permission : $permission")

                if (ContextCompat.checkSelfPermission(
                        askActivity.get()!!.baseContext,
                        permission
                    ) == PackageManager.PERMISSION_DENIED
                ) {

                    denied.add(permission)

                } else if (ContextCompat.checkSelfPermission(askActivity.get()!!, permission)
                    == PackageManager.PERMISSION_GRANTED
                ) {

                    Log.d(TAG, "granted : $permission")
                    granted.add(permission)
                }
            }


            if (denied.size > 0) {

                askActivity.get()?.let {
                    Log.d(TAG, "askActivity is not null")
                    val intent = Intent(it.applicationContext, PermissionActivity::class.java)
                    intent.putExtra(REQUEST_CODE_KEY, requestCode)
                    intent.putStringArrayListExtra(REQUEST_PERMISSIONS, denied)
                    if (permanentlyDenied.isNotEmpty()) {
                        intent.putStringArrayListExtra(
                            REQUEST_PERM_DENIED_PERMISSIONS,
                            permanentlyDenied
                        )
                    }

                    it.startActivity(intent)
                } ?: Log.d(TAG, "askActivity not null")

            } else {
                if (granted.isNotEmpty()) {
                    callback.get()?.permissionsGranted(requestCode, granted.toTypedArray())
                }

                if (permanentlyDenied.isNotEmpty()) {
                    callback.get()
                        ?.permissionsPermDenied(requestCode, permanentlyDenied.toTypedArray())
                }
            }
        }

    }

    internal fun permissionResult(
        requestCode: Int,
        grants: Array<String>,
        denied: Array<String>?,
        permanentlyDenied: Array<String>?
    ) {
        if (grants.isNotEmpty()) {
            callback.get()?.permissionsGranted(requestCode, grants)
        }

        denied?.let {
            if (it.isNotEmpty()) {
                callback.get()?.permissionsDenied(requestCode, it)
            }
        }

        permanentlyDenied?.let {
            if (it.isNotEmpty()) {
                callback.get()?.permissionsPermDenied(requestCode, it)
            }
        }
    }

    /**
     *  when PermissionActivity's onDestroy has been called
     *
     */
    internal fun onActivityDestroy() {
        preference?.apply {
            edit().putBoolean(PERMISSION_FIRST_TIME_CHECK, false).apply()
        }
    }

    interface PermissionCallback {
        fun permissionsGranted(requestCode: Int, permissions: Array<String>)
        fun permissionsDenied(requestCode: Int, permissions: Array<String>)
        fun permissionsPermDenied(requestCode: Int, permissions: Array<String>)
    }
}