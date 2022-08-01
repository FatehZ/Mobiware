package com.ktxdevelopment.mobiware.clients

import android.content.Context
import android.content.SharedPreferences
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.util.Constants


object PreferenceClient {

    // Only Read // Do Not Modify

    private lateinit var editor: SharedPreferences.Editor

    private suspend fun instantiate(context: Context) {
        editor = getCurrent(context).edit()
    }
    private suspend fun getCurrent(context: Context) : SharedPreferences { return context.getSharedPreferences(Constants.PREFERENCE_LOCK_KEY, Context.MODE_PRIVATE) }


    suspend fun saveUserDetailsToPreferences(context: Context, user: LocalUser) {
        instantiate(context)
        editor.putString(Constants.PR_userId, user.userId)
        editor.putString(Constants.PR_username, user.username)
        editor.putString(Constants.PR_mobileNumberBase, user.mobileNumberBase)
        editor.putString(Constants.PR_mobileNumberCountryCode, user.mobileCountryCode)
        editor.putStringSet(Constants.PR_mobileId, user.mobileId.toSet())
        editor.putString(Constants.PR_email, user.email)
        editor.putString(Constants.PR_imageBase64, user.image64)
        editor.putString(Constants.PR_imageOnline, user.imageOnline)
        editor.apply()
    }

    suspend fun getUserDetailsFromPreferences(context: Context): LocalUser {
        instantiate(context)
        var user: LocalUser
        getCurrent(context).apply {
            user = LocalUser(
                getString(Constants.PR_userId,"")!!,
                getString(Constants.PR_username,"")!!,
                getString(Constants.PR_mobileNumberBase,"")!!,
                getString(Constants.PR_mobileNumberCountryCode,"994")!!,
                (getStringSet(Constants.PR_mobileId, setOf())!!).toList(),
                getString(Constants.PR_email,"")!!,
                getString(Constants.PR_imageBase64,"")!!,
                getString(Constants.PR_imageOnline,"")!!
            )
        }
        return user
    }
}