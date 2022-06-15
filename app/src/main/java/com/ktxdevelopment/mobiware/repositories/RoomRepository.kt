package com.ktxdevelopment.mobiware.repositories

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ktxdevelopment.mobiware.db.MobileDao
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import javax.inject.Inject

class RoomRepository @Inject constructor(private var dao: MobileDao) {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(data: RoomPhoneModel) = dao.upsertPhone(data)

    @Query("SELECT * FROM mobile_table")
    suspend fun getMobile() = dao.getPhones()[0]
}