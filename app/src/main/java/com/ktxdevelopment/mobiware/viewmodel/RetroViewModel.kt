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
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RetroViewModel @Inject constructor(private var restRepo: RetrofitRepository, application: Application) : AndroidViewModel(application) {


     private val _devicesByBrand =  MutableLiveData<Resource<SearchResponse>>()
     val devicesByBrand = _devicesByBrand

     fun searchByBrand(brandSlug: String, page: Int) {
          viewModelScope.launch {
               restRepo.searchByBrand(brandSlug, page).collect{
                    _devicesByBrand.value = it
               }
          }
     }


     var searchResponse: MutableLiveData<Resource<SearchResponse>> = MutableLiveData()
     fun searchMobiles(query: String) {
          viewModelScope.launch { searchResponse.postValue(restRepo.searchMobile(query)) }
     }


     var getResponse: MutableLiveData<Resource<GetResponse>> = MutableLiveData()
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

     var getMyDeviceResponse: MutableLiveData<ArrayList<Resource<GetResponse>>> = MutableLiveData(ArrayList())
     fun getMyDevices(urlList: ArrayList<String>) {
          for (i in urlList) { viewModelScope.launch { getMyDeviceResponse.postValue(getMyDeviceResponse.value!!.apply { add(restRepo.getMyDevices(i)) }) } }
     }
}