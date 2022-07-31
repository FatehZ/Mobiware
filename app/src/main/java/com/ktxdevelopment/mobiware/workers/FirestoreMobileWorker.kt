package com.ktxdevelopment.mobiware.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.util.Constants
import kotlin.coroutines.suspendCoroutine
import com.ktxdevelopment.mobiware.models.rest.product.Data as DataMobile

class FirestoreMobileWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        val str = inputData.getString(Constants.PHONE_EXTRA)
        val mobile: DataMobile = Gson().fromJson(str, DataMobile::class.java)

        return try {
            FirebaseClient.sendMobileDataInBack(mobile)
            return Result.success()
        }catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Data.Builder().putString("error", e.stackTraceToString()).build())
        }
    }
}