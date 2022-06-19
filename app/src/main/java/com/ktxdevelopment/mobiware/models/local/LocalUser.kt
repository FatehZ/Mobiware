package com.ktxdevelopment.mobiware.models.local

import android.graphics.Bitmap

data class LocalUser(
    var userId: String = "",
    var username: String = "",
    var mobileNumber: String = "",
    var mobileId: List<String> = ArrayList(),
    var email: String = "",
    var image: String = ""
)