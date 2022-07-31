package com.ktxdevelopment.mobiware.models.rest


sealed class Resource<T>(
    val data: T? = null,
    val error: Exception? = null
) {

    class Success<T>(data: T) : Resource<T>(data = data)

    class Error<T>(mError: Exception) : Resource<T>(error = mError)

    class Loading<T> : Resource<T>()
}