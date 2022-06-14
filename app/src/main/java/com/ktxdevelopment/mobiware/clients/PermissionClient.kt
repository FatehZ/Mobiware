package com.ktxdevelopment.mobiware.clients

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity

object PermissionClient {

    fun hasGalleryPermissions(context: Context) : Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun getFileExtension(activity: BaseActivity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}