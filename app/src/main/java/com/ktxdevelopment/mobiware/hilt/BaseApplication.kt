package com.ktxdevelopment.mobiware.hilt

import android.app.Application
import com.ktxdevelopment.mobiware.clients.BaseClient.setDarkMode
import com.ktxdevelopment.mobiware.util.Constants.isDarkMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setDarkMode(isDarkMode)
    }
}