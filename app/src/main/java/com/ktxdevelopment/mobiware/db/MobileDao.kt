package com.ktxdevelopment.mobiware.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel

@Dao
interface MobileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPhone(mobileJson: RoomPhoneModel)

    @Query("SELECT * FROM mobile_table")
    suspend fun getPhones() : List<RoomPhoneModel>

    @Query("DELETE FROM mobile_table")
    suspend fun clearDB()

}