package com.ktxdevelopment.mobiware.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.models.firebase.FireFeedback
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.repositories.LocalRepository
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.workers.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ktxdevelopment.mobiware.models.rest.product.Data as DataMobile

@HiltViewModel
class LocalViewModel @Inject constructor(private var localRepo: LocalRepository, application: Application) : AndroidViewModel(application) {

     var roomMobileDetails: MutableLiveData<RoomPhoneModel> = MutableLiveData()
     var localUser: MutableLiveData<LocalUser> = MutableLiveData()

     fun getRoomMobileDetails(){
          viewModelScope.launch {
               roomMobileDetails.postValue(localRepo.getMobile())
          }
     }


     fun getLocalUser(context: Context) {
          viewModelScope.launch {
               localUser.postValue(localRepo.getLocalUser(context))
          }
     }


     fun runSynchronous(function: () -> Unit) {
          viewModelScope.launch(Dispatchers.Default) {
               function()
          }
     }



     fun clearDatabaseWithWork(context: Context) {
          viewModelScope.launch {
               val roomClearRequest = OneTimeWorkRequest.Builder(RoomClearLocalWorker::class.java).build()
               WorkManager.getInstance(context).enqueue(roomClearRequest)
          }
     }


     fun writeMobileToRoomDB(context: Context, mobile: DataMobile, slug: String) {

          viewModelScope.launch(Dispatchers.IO) {
               val data: Data = Data.Builder()
                    .putString(Constants.PHONE_EXTRA, Gson().toJson(mobile))
                    .putString(Constants.SLUG_EXTRA, slug)
                    .build()

               val roomMobileRequest = OneTimeWorkRequest.Builder(RoomMobileWorker::class.java).setInputData(data).build()

               WorkManager.getInstance(context).enqueue(roomMobileRequest)
          }
     }


     fun writeUserToPreferences(context: Context, user: LocalUser, slug: String = "") {

          viewModelScope.launch(Dispatchers.IO) {
               val data = Data.Builder()
                    .putString(Constants.LOCAL_USER, Gson().toJson(user))
                    .putString(Constants.PR_currentSlug, slug)
                    .build()

               val preferenceUserWorker = OneTimeWorkRequest.Builder(PreferenceUserWorker::class.java).setInputData(data).build()

               WorkManager.getInstance(context).enqueue(preferenceUserWorker)
          }
     }


     fun writeFeedbackPhotosToFirestore(context: Context, list: ArrayList<Uri>, feedback: FireFeedback) {

          viewModelScope.launch(Dispatchers.IO) {

               for (i in 1..list.size) {
                    val data = Data.Builder()
                         .putString(Constants.FEEDBACK_EXTRA, Gson().toJson(feedback))
                         .putString(Constants.IMAGE_EXTRA, list[i-1].toString())
                         .build()

                    val feedbackWorker = OneTimeWorkRequest.Builder(FirestoreFeedbackWorker::class.java)
                         .setInputData(data)
                         .build()

                    WorkManager.getInstance(context).enqueue(feedbackWorker)
               }
          }
     }

     fun deleteUserFromFirestore(context: Context, userId: String) {

          viewModelScope.launch(Dispatchers.IO) {
               val data: Data = Data.Builder()
                    .putString(Constants.ID_EXTRA, userId)
                    .build()

               val deleteUserRequest = OneTimeWorkRequest.Builder(FirestoreDeleteUserWorker::class.java)
                    .setInputData(data)
                    .build()

               WorkManager.getInstance(context).enqueue(deleteUserRequest)
          }
     }

     fun deleteUnusedUserProfileImageFromFirestore(context: Context, imageUrlToDelete: String) {

          viewModelScope.launch(Dispatchers.IO) {
               val data = Data.Builder()
                    .putString(Constants.REF_EXTRA, imageUrlToDelete)
                    .build()

               val deleteImageRequest = OneTimeWorkRequest.Builder(FirestoreDeleteImageWorker::class.java)
                         .setInputData(data)
                         .build()

               WorkManager.getInstance(context).enqueue(deleteImageRequest)
          }
     }
}