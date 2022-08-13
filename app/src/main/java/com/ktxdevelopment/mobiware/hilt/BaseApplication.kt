package com.ktxdevelopment.mobiware.hilt

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.ktxdevelopment.mobiware.clients.main.PreferenceClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(applicationContext)
//        AppCompatDelegate.setDefaultNightMode(PreferenceClient.getCurrentThemeMode(this))
    }
}