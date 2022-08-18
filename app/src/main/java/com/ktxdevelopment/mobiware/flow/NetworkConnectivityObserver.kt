package com.ktxdevelopment.mobiware.flow

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class NetworkConnectivityObserver(context: Context) : ConnectivityObserver{

     private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

     override fun observe(): Flow<ConnectivityObserver.ConnectionStatus> {
          return callbackFlow {
               val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                         super.onAvailable(network)
                         launch { send(ConnectivityObserver.ConnectionStatus.Available) }
                    }

                    override fun onLost(network: Network) {
                         super.onLost(network)
                         launch { send(ConnectivityObserver.ConnectionStatus.Lost) }
                    }

                    override fun onUnavailable() {
                         super.onUnavailable()
                         launch { send(ConnectivityObserver.ConnectionStatus.Unavailable) }
                    }
               }
               connectivityManager.registerDefaultNetworkCallback(callback)
               awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
               }
          }
     }
}