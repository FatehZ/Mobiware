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


     fun clearDatabase() {
          viewModelScope.launch {
               localRepo.clearDB()
          }
     }



     fun clearDatabaseWithWork(context: Context) {
          viewModelScope.launch {
               val roomClearRequest = OneTimeWorkRequest.Builder(RoomClearLocalWorker::class.java).build()
               WorkManager.getInstance(context).enqueue(roomClearRequest)
          }
     }


     fun writeMobileToRoomDB(context: Context, mobile: DataMobile) {

          viewModelScope.launch {
               val data: Data = Data.Builder()
                    .putString(Constants.PHONE_EXTRA, Gson().toJson(mobile))
                    .build()

               val roomMobileRequest = OneTimeWorkRequest.Builder(RoomMobileWorker::class.java)
                    .setInputData(data)
                    .build()

               WorkManager.getInstance(context).enqueue(roomMobileRequest)
          }
     }


     fun writeMobileToFirestore(context: Context, mobile: DataMobile) {

          viewModelScope.launch {
               val data: Data = Data.Builder()
                    .putString(Constants.PHONE_EXTRA, Gson().toJson(mobile))
                    .build()

               val fireMobileRequest = OneTimeWorkRequest.Builder(FirestoreMobileWorker::class.java)
                    .setInputData(data)
                    .build()

               WorkManager.getInstance(context).enqueue(fireMobileRequest)
          }
     }


     fun writeUserToPreferences(context: Context, user: LocalUser) {

          viewModelScope.launch {
               val data = Data.Builder()
                    .putString(Constants.LOCAL_USER, Gson().toJson(user))
                    .build()

               val preferenceUserWorker = OneTimeWorkRequest.Builder(PreferenceUserWorker::class.java)
                    .setInputData(data)
                    .build()

               WorkManager.getInstance(context).enqueue(preferenceUserWorker)
          }
     }


     fun writeFeedbackPhotosToFirestore(context: Context, list: ArrayList<Uri>, feedback: FireFeedback) {

          viewModelScope.launch {

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
}