package com.ktxdevelopment.mobiware.repositories

import android.content.Context
import com.ktxdevelopment.mobiware.clients.main.PreferenceClient
import com.ktxdevelopment.mobiware.db.MobileDao
import javax.inject.Inject

class LocalRepository @Inject constructor(private var dao: MobileDao) {

    suspend fun getMobile() = dao.getPhones()[0]

    suspend fun getLocalUser(context: Context) = PreferenceClient.getUserDetailsFromPreferences(context)

}