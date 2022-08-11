package com.ktxdevelopment.mobiware.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.ktxdevelopment.mobiware.util.Constants

class FirestoreDeleteUserWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

     override suspend fun doWork(): Result {
          val id: String = inputData.getString(Constants.ID_EXTRA).toString()

          return try {
               FirebaseFirestore.getInstance().collection(id).document().delete()
               Result.success()
          }catch (e: Exception) {
               Result.retry()
          }
     }
}