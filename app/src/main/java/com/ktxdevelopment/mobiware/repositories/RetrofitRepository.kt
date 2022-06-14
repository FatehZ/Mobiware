package com.ktxdevelopment.mobiware.repositories

import com.ktxdevelopment.mobiware.retrofit.RetrofitApi
import javax.inject.Inject

class RetrofitRepository @Inject constructor(private var api: RetrofitApi) {

    suspend fun searchMobile(query: String) = api.searchMobile(query)

    suspend fun getMobile(url: String) = api.getMobile(url)

}