package com.ktxdevelopment.mobiware.repositories

import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.product.GetResponse
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.retrofit.RetrofitApi
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RetrofitRepository @Inject constructor(private var api: RetrofitApi) : BaseRepository() {


     suspend fun searchMobile(query: String): Resource<SearchResponse> = safeApiCall { api.searchMobile(query) }

     suspend fun getMobile(url: String): Resource<GetResponse> = safeApiCall { api.getMobile(url) }

     suspend fun getMyDevices(url: String): Resource<GetResponse> = safeApiCall { api.getMobile(url) }

     suspend fun searchLatest(): Resource<SearchResponse> = safeApiCall { api.searchLatest() }

     suspend fun searchByBrand(brandSlug: String, page: Int) = flow {
          emit(Resource.Loading())
          emit(safeApiCall { api.searchByBrand(brandSlug, page) })
     }
}