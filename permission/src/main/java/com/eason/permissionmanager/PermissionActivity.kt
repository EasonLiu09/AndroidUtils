package com.eason.permissionmanager

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker

class PermissionActivity : AppCompatActivity() {

    private lateinit var permissions: ArrayList<String>
    private var permDeniedPermissions: ArrayList<String>? = null
    private var mRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            it.extras?.let { extra ->
                permissions = extra.getStringArrayList(PermissionManager.REQUEST_PERMISSIONS)!!
                mRequestCode = extra.getInt(PermissionManager.REQUEST_CODE_KEY)

                if (extra.containsKey(PermissionManager.REQUEST_PERM_DENIED_PERMISSIONS)) {
                    permDeniedPermissions =
                        extra.getStringArrayList(PermissionManager.REQUEST_PERM_DENIED_PERMISSIONS)
                }
            }
        }

        requestPermissions(permissions.toTypedArray(), mRequestCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        PermissionManager.onActivityDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted = ArrayList<String>()
        val denied = ArrayList<String>()
        val permanentlyDenied = ArrayList<String>()


        if (requestCode == mRequestCode) {

            for (index in grantResults.indices) {

                when (grantResults[index]) {

                    PermissionChecker.PERMISSION_GRANTED -> granted.add(permissions[index])
                    PermissionChecker.PERMISSION_DENIED -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(permissions[index])) {
                                denied.add(permissions[index])
                            } else {
                                permanentlyDenied.add(permissions[index])
                            }
                        } else {
                            denied.add(permissions[index])
                        }
                    }
                }
            }

            permDeniedPermissions?.let {
                permanentlyDenied.addAll(it.toTypedArray())
            }

            PermissionManager.permissionResult(
                mRequestCode,
                granted.toTypedArray(),
                denied.toTypedArray(),
                permanentlyDenied.toTypedArray()
            )
            finish()
        }
    }
}