package com.ktxdevelopment.mobiware.models.firebase

data class FireFeedback(
    var feedId: String,
    var userId: String,
    var mail: String = "",
    var message: String = "",
    var photos: ArrayList<String> = ArrayList()
)