package com.ktxdevelopment.mobiware.models.local

data class LocalUser(
    var userId: String = "",
    var username: String = "",
    var mobileNumber: String = "",
    var mobileId: List<String> = ArrayList(),
    var email: String = "",
)