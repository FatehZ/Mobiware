package com.ktxdevelopment.mobiware.hilt

import android.app.Application
import androidx.work.Configuration
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(applicationContext)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}