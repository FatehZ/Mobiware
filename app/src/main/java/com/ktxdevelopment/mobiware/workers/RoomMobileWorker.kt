package com.ktxdevelopment.mobiware.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.db.MobileDatabase
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.util.Constants


class RoomMobileWorker (appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val dao by lazy { MobileDatabase.getInstance(appContext).getDao() }

    override suspend fun doWork(): Result {

        val str = inputData.getString(Constants.PHONE_EXTRA)
        val mobile: Data = Gson().fromJson(str, Data::class.java)

        return try {
            dao.upsertPhone(RoomPhoneModel(mobile.phone_name ,mobile))
            Result.success()
        }catch (e: Exception) { Result.retry() }
    }
}