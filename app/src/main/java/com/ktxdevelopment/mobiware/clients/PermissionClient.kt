package com.ktxdevelopment.mobiware.clients

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Base64
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import java.io.ByteArrayOutputStream
import java.net.URL


object PermissionClient {

    fun hasGalleryPermissions(context: Context) : Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun getFileExtension(activity: BaseActivity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun getBaseImageFromString(url: String): String {
        if (url.isEmpty()) { return "" }
        val newUrl: URL
        val bitmap: Bitmap
        var base64 = ""
        try {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            newUrl = URL(url)
            bitmap = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream())
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return base64
    }
}