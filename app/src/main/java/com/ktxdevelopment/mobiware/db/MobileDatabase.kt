package com.ktxdevelopment.mobiware.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel

@Database(entities = [RoomPhoneModel::class], version = 1, exportSchema = false)
@TypeConverters(MobileConverter::class)
abstract class MobileDatabase : RoomDatabase() {
    abstract fun getDao() : MobileDao
}