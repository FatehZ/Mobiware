package com.ktxdevelopment.mobiware.repositories

import android.content.Context
import com.ktxdevelopment.mobiware.clients.Preferences
import com.ktxdevelopment.mobiware.db.MobileDao
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import javax.inject.Inject

class LocalRepository @Inject constructor(private var dao: MobileDao) {

    suspend fun upsert(data: RoomPhoneModel) = dao.upsertPhone(data)

    suspend fun getMobile() = dao.getPhones()[0]

    suspend fun saveLocalUser(context: Context, fireUser: FireUser) { Preferences.saveUserDetailsToPreferences(context, fireUser) }

    suspend fun getLocalUser(context: Context) = Preferences.getUserDetailsFromPreferences(context)

    suspend fun clearDB() { dao.clearDB() }
}