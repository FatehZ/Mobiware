package com.ktxdevelopment.mobiware.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.util.Constants

@Database(entities = [RoomPhoneModel::class], version = 1, exportSchema = false)
@TypeConverters(MobileConverter::class)
abstract class MobileDatabase : RoomDatabase() {

    abstract fun getDao() : MobileDao


    companion object{
        private var instance: MobileDatabase? = null

        fun getInstance(context: Context): MobileDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context,MobileDatabase::class.java, Constants.DB_NAME)
                    .build()
            }
            return instance!!
        }
    }
}