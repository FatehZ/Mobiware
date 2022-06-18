package com.ktxdevelopment.mobiware.repositories

import com.ktxdevelopment.mobiware.db.MobileDao
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import javax.inject.Inject

class RoomRepository @Inject constructor(private var dao: MobileDao) {

    suspend fun upsert(data: RoomPhoneModel) = dao.upsertPhone(data)

    suspend fun getMobile() = dao.getPhones()[0]
}