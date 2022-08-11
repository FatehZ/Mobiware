package com.ktxdevelopment.mobiware.clients.main

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ktxdevelopment.mobiware.BuildConfig
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.util.Constants
import kotlin.math.roundToInt

object RemoteClient {

    fun checkMustUpdate(): HashMap<String, Any> {

        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 21600 }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val map: HashMap<String, Any> = HashMap()
        map[Constants.ANY_UPDATE] = false
        map[Constants.UPDATE_SIGN] = Constants.MAY

        remoteConfig.fetch().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate()
                    val newVersion: Int = (remoteConfig.getDouble(Constants.VERSION)).roundToInt()
                    if (isHigherVersion(newVersion)) {
                        map[Constants.ANY_UPDATE] = true
                        map[Constants.UPDATE_SIGN] = remoteConfig.getString(Constants.UPDATE_SIGN)
                    }
                }
            }
        return map
    }


    private fun isHigherVersion(new: Int): Boolean {
        return try {
            val l1: List<String> = ((BuildConfig.VERSION_NAME).split("."))
            var n1 = ""
            for (i in l1) { n1 += i }
            val cur = n1.toInt()

            new > cur
        } catch (e: Exception) { false }
    }
}