package com.ktxdevelopment.mobiware.clients.firebase

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.ui.activities.SignInActivity
import com.ktxdevelopment.mobiware.ui.activities.SignUpActivity

object ErrorClient {

    fun registerError(context: SignUpActivity, ex: Exception) {
        context.hideProgressDialog()
        when (ex) {
            is FirebaseAuthUserCollisionException -> {
                context.showErrorSnackbar(context.getString(R.string.user_exists))
            }
            else -> {
                context.showErrorSnackbar(context.getString(R.string.error_occurred))
            }
        }
    }


    fun signInError(context: SignInActivity, ex: Exception) {
        context.hideProgressDialog()
        when (ex) {
            is FirebaseAuthInvalidUserException -> {
                context.showErrorSnackbar(context.getString(R.string.user_does_not_exist))
            }
            is FirebaseAuthInvalidCredentialsException -> {
                context.showErrorSnackbar(context.getString(R.string.password_is_wrong))
            }
            else -> {
                context.showErrorSnackbar(context.getString(R.string.error_occurred))
            }
        }
    }
}