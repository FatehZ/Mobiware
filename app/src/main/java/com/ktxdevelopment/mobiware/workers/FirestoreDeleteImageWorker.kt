package com.ktxdevelopment.mobiware.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import com.ktxdevelopment.mobiware.util.Constants

class FirestoreDeleteImageWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

     override suspend fun doWork(): Result {

          val stRef = inputData.getString(Constants.REF_EXTRA).toString()

          return try {
               FirebaseStorage.getInstance().getReference("profile_images").child(stRef).delete()
               Result.success()
          }catch (e: Exception) { Result.failure() }
     }
}