package com.ktxdevelopment.mobiware.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.repositories.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private var roomRepo: RoomRepository,
    application: Application) : AndroidViewModel(application) {

    var roomMobileDetails: MutableLiveData<RoomPhoneModel> = MutableLiveData()


    suspend fun getRoomMobileDetails() {
        viewModelScope.launch {
            roomMobileDetails.value = roomRepo.getMobile()
        }
    }

    suspend fun upsertMobile(model: RoomPhoneModel) = roomRepo.upsert(model)
}