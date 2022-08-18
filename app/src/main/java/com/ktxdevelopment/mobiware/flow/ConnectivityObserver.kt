package com.ktxdevelopment.mobiware.flow

import kotlinx.coroutines.flow.Flow


interface ConnectivityObserver {

     fun observe() : Flow<ConnectionStatus>

     enum class ConnectionStatus {
          Available, Unavailable, Lost
     }
}