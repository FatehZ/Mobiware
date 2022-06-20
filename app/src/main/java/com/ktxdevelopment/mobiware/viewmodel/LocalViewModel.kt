package com.ktxdevelopment.mobiware.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.repositories.LocalRepository
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.workers.FirestoreMobileWorker
import com.ktxdevelopment.mobiware.workers.RoomMobileWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ktxdevelopment.mobiware.models.rest.product.Data as DataMobile

@HiltViewModel
class LocalViewModel @Inject constructor(private var localRepo: LocalRepository, application: Application) : AndroidViewModel(application) {

    var roomMobileDetails: MutableLiveData<RoomPhoneModel> = MutableLiveData()
    var localUser: MutableLiveData<LocalUser> = MutableLiveData()

    fun getRoomMobileDetails() {
        viewModelScope.launch {
            roomMobileDetails.value = localRepo.getMobile()
        }
    }

    fun upsertMobile(model: RoomPhoneModel) = viewModelScope.launch {
        localRepo.upsert(model)
    }

    fun getLocalUser(context: Context) {
        viewModelScope.launch {
            localUser.postValue(localRepo.getLocalUser(context))
        }
    }

    fun saveLocalUser(context: Context, fireUser: FireUser) {
        viewModelScope.launch {
            localRepo.saveLocalUser(context, fireUser)
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            localRepo.clearDB()
        }
    }



    val TAG = "VM_TAG"

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
}