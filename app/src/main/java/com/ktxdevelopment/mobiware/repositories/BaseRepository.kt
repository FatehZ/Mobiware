package com.ktxdevelopment.mobiware.repositories

import com.ktxdevelopment.mobiware.clients.exceptions.RequestBodyEmptyException
import com.ktxdevelopment.mobiware.clients.exceptions.RequestUnsuccessfulException
import com.ktxdevelopment.mobiware.models.rest.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

open class BaseRepository {

    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()

                if (response.isSuccessful) {
                    if (response.body()!= null) {
                        Resource.Success(data = response.body()!!)
                    }else{
                        Resource.Error(RequestBodyEmptyException())
                    }
                }else {
                    Resource.Error(mError = RequestUnsuccessfulException())
                }

            } catch (e: Exception) {
                Resource.Error(e)
            }
        }
    }
}
