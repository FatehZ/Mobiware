package com.ktxdevelopment.mobiware.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.PreferenceClient
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.util.Constants
import okhttp3.internal.wait

class PreferenceUserWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

     override suspend fun doWork(): Result {

          val user: LocalUser = BaseClient.convertFireToLocalUser(Gson().fromJson(inputData.getString(Constants.LOCAL_USER), LocalUser::class.java))
          val slug: String? = inputData.getString(Constants.PR_currentSlug)

          return try {
               PreferenceClient.saveUserDetailsToPreferences(applicationContext, user, slug)
               Result.success()
          } catch (e: Throwable) { Result.failure() }
     }
}