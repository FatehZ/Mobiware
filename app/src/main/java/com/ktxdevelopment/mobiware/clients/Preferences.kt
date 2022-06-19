package com.ktxdevelopment.mobiware.clients

import android.content.Context
import android.content.SharedPreferences
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.util.Constants


object Preferences {

    // Only Read // Do Not Modify

    private lateinit var editor: SharedPreferences.Editor

    private suspend fun instantiate(context: Context) {
        editor = getCurrent(context).edit()
    }
    private suspend fun getCurrent(context: Context) : SharedPreferences { return context.getSharedPreferences(Constants.PREFERENCE_LOCK_KEY, Context.MODE_PRIVATE) }



    // ACCESS

    suspend fun saveUserDetailsToPreferences(context: Context, fUser: FireUser) {
        instantiate(context)
        val user = LocalUser(fUser.userId, fUser.username, fUser.mobileNumber, fUser.mobileId, fUser.email, fUser.imageUrl)
        editor.putString(Constants.PR_imageString, user.image)
        editor.putString(Constants.PR_userId, user.userId)
        editor.putString(Constants.PR_username, user.username)
        editor.putString(Constants.PR_mobileNumber, user.mobileNumber)
        editor.putStringSet(Constants.PR_mobileId, user.mobileId.toSet())
        editor.putString(Constants.PR_email, user.email)
        editor.apply()
    }

    suspend fun getUserDetailsFromPreferences(context: Context): LocalUser {
        instantiate(context)
        var user: LocalUser
        getCurrent(context).apply {
            user = LocalUser(
                getString(Constants.PR_userId,"")!!,
                getString(Constants.PR_username,"")!!,
                getString(Constants.PR_mobileNumber,"")!!,
                (getStringSet(Constants.PR_mobileId, setOf())!!).toList(),
                getString(Constants.PR_email,"")!!
            )
        }
        return user
    }

    suspend fun clearPreferences(context: Context) {
        instantiate(context)
        editor.clear()
        editor.apply()
    }
}