package com.ktxdevelopment.mobiware.workers

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.clients.main.PermissionClient
import com.ktxdevelopment.mobiware.models.firebase.FireFeedback
import com.ktxdevelopment.mobiware.util.Constants

class FirestoreFeedbackWorker(val appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        val feed: FireFeedback = Gson().fromJson(inputData.getString(Constants.FEEDBACK_EXTRA), FireFeedback::class.java)
        val uri: Uri = Uri.parse(inputData.getString(Constants.IMAGE_EXTRA))


        val sRef: StorageReference = FirebaseStorage.getInstance().getReference("feedback_images").child(
            "FEED_IMAGE_" + System.currentTimeMillis() + "." + PermissionClient.getFileExtension(appContext, uri))

        sRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                FirebaseFirestore.getInstance().collection(Constants.FEEDBACKS).document(feed.feedId)
                    .update(Constants.PHOTOS, FieldValue.arrayUnion(uri.toString()))
            }
        }
        return Result.success()
    }
}