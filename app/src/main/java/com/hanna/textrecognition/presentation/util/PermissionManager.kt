package com.hanna.textrecognition.presentation.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hanna.textrecognition.R
import javax.inject.Inject


class PermissionManager @Inject constructor(
    private val context: Context
) {

    lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    fun checkPermissionRequest(
        activity: Activity,
        callback: () -> Unit
    ) {
        val newPermissionList = mutableListOf<String>()
        val rationalePermission = mutableListOf<String>()

        for (permission in REQUIRED_PERMISSIONS) {
            if (shouldShowPermissionRationale(activity, permission)) {
                rationalePermission.add(permission)
            } else if (!hasPermission(permission)) {
                newPermissionList.add(permission)
            }
        }

        when {
            rationalePermission.isNotEmpty() -> {
                showPermissionDialog(activity)
            }
            newPermissionList.isNotEmpty() -> {
                permissionsLauncher.launch(newPermissionList.toTypedArray())
            }
            else -> callback.invoke()
        }
    }

    fun showPermissionDialog(activity: Activity) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(context.getString(R.string.title_permission_required))
            .setMessage(context.getString(R.string.caption_permission_required))
            .setPositiveButton(context.getString(android.R.string.ok)) { _, _ ->
                activity.finish()
            }
            .setNegativeButton(context.getString(R.string.text_go_to_setting)) { _, _ ->
                goToSetting(activity)
            }
            .show()
    }

    private fun goToSetting(activity: Activity) {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        with(intent) {
            data = Uri.fromParts("package", context.packageName, null)
            addCategory(CATEGORY_DEFAULT)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }

        activity.startActivity(intent)
    }

    private fun shouldShowPermissionRationale(activity: Activity, permission: String): Boolean {
        return shouldShowRequestPermissionRationale(activity, permission)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}