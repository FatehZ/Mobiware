package com.ktxdevelopment.mobiware.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.models.rest.product.Data

class MobileConverter {

    @TypeConverter
    fun fromData(dt: Data?): String {
        return Gson().toJson(dt)
    }


    @TypeConverter
    fun toData(str: String): Data? {
        return Gson().fromJson(str, Data::class.java)
    }
}