package com.ktxdevelopment.mobiware.models.firebase

data class FireUser(
    var userId: String = "",
    var username: String = "",
    var imageUrl: String = "",
    var mobileNumber: String = "",
    var mobileId: List<String> = ArrayList(),
    var email: String = "",
    var feedbackIds: List<String> = ArrayList()
)