package com.ktxdevelopment.mobiware.clients.firebase

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity

object ErrorClient {

    fun registerError(context: BaseActivity, ex: Exception) {
        context.hideProgressDialog()
        when (ex) {
            is FirebaseAuthUserCollisionException -> {
                context.hideProgressDialog()
                context.showErrorSnackbar(context.getString(R.string.user_exists))
            }
            else -> {
                context.hideProgressDialog()
                context.showErrorSnackbar(context.getString(R.string.error_occurred))
            }
        }
    }


    fun signInError(context: BaseActivity, ex: Exception) {
        context.hideProgressDialog()
        when (ex) {
            is FirebaseAuthInvalidUserException -> {
                context.hideProgressDialog()
                context.showErrorSnackbar(context.getString(R.string.user_does_not_exist))
            }
            is FirebaseAuthInvalidCredentialsException -> {
                context.hideProgressDialog()
                context.showErrorSnackbar(context.getString(R.string.password_is_wrong))
            }
            else -> {
                context.hideProgressDialog()
                context.showErrorSnackbar(context.getString(R.string.error_occurred))
            }
        }
    }
}