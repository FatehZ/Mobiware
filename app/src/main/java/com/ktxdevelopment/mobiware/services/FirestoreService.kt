package com.ktxdevelopment.mobiware.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class FirestoreService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        Log.i("SRV_TAG", "onStartCommand: continues")
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("SRV_TAG", "onStartCommand: continues")
        return super.onStartCommand(intent, flags, startId)
    }
}