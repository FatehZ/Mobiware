package com.ktxdevelopment.mobiware.models.firebase

import com.ktxdevelopment.mobiware.models.rest.product.Data

data class FireMobile(
    var phoneDetails: Data,
    var userId: ArrayList<String> = ArrayList()
)
