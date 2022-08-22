package com.ktxdevelopment.mobiware.retrofit

import com.ktxdevelopment.mobiware.models.rest.product.GetResponse
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.Constants.SEARCH_URL
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {

     @GET(SEARCH_URL)
     suspend fun searchMobile(
          @Query("query") query: String
     ): Response<SearchResponse>


     @GET
     suspend fun getMobile(
          @Url url: String
     ) : Response<GetResponse>


     @GET(Constants.LATEST_URL)
     suspend fun searchLatest() : Response<SearchResponse>

     @GET(Constants.BRANDS_URL +"/{slug}")
     suspend fun searchByBrand(
          @Path("slug") brandSlug: String,
          @Query("page") page: Int
     ) : Response<SearchResponse>
}