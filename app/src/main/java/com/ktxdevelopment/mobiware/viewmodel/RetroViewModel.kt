package com.ktxdevelopment.mobiware.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.product.GetResponse
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.repositories.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RetroViewModel @Inject constructor(private var restRepo: RetrofitRepository, application: Application) : AndroidViewModel(application) {

    var searchResponse: MutableLiveData<Resource<SearchResponse>> = MutableLiveData()
    var getResponse: MutableLiveData<Resource<GetResponse>> = MutableLiveData()
    var getMyDeviceResponse: MutableLiveData<Resource<GetResponse>> = MutableLiveData()

    fun searchMobile(query: String) {
        viewModelScope.launch {
            searchResponse.postValue(restRepo.searchMobile(query))
        }
    }

    fun getMobile(url: String) {
        viewModelScope.launch {
            getResponse.postValue(restRepo.getMobile(url))
        }
    }

    fun searchLatest() {
        viewModelScope.launch {
            searchResponse.postValue(restRepo.searchLatest())
        }
    }

    fun getMyDevices(url: String) {
        viewModelScope.launch {
            getMyDeviceResponse.postValue(restRepo.getMyDevices(url))
        }
    }

}