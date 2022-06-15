package com.ktxdevelopment.mobiware.clients

import android.content.Context
import android.content.SharedPreferences
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.util.Constants


object Preferences {

    // Only Read // Do Not Modify
    private lateinit var editor: SharedPreferences.Editor
    fun instantiate(context: Context) { editor = getCurrent(context).edit() }
    private fun getCurrent(context: Context) : SharedPreferences { return context.getSharedPreferences(Constants.PREFERENCE_LOCK_KEY, Context.MODE_PRIVATE) }



    fun storeIsFirstRun(isFirstRun: Boolean) {
        editor.putBoolean(Constants.FIRST_RUN, isFirstRun)
        editor.apply()
    }

    fun getIsFirstRun(context: Context) : Boolean {
        return getCurrent(context).getBoolean(Constants.FIRST_RUN, true)
    }

    // Activity Checking Functions

    fun checkIsFirstRun(context: Context) : Boolean {
        instantiate(context)
        return getIsFirstRun(context)
    }

    fun saveUserDetailsToPreferences(user: FireUser) {

    }

    fun getUserDetailsFromPreferences() {

    }
}