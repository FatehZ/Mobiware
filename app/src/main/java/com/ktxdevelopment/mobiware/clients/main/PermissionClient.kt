package com.ktxdevelopment.mobiware.clients.main

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.OpenableColumns
import android.util.Base64
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.net.URL


object PermissionClient {

    fun hasGalleryPermissions(context: Context) : Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun getFileExtension(activity: Context, uri: Uri?): String? {
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
        } catch (e: Exception) { }
        return base64
    }

    fun getBitmapFromBase64(strBase64: String): Bitmap? {
        val decodedString: ByteArray = Base64.decode(strBase64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }


    fun getImageFileName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        if (name == null) {
            return "Image"
        }
        return name
    }
}