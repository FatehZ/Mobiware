package com.ktxdevelopment.mobiware.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ktxdevelopment.mobiware.db.MobileDatabase

class RoomClearLocalWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val dao by lazy { MobileDatabase.getInstance(appContext).getDao() }

    override suspend fun doWork(): Result {
        return try {
            dao.clearDB()
            Result.success()
        }catch (e: Exception) {
            Result.failure()
        }
    }
}