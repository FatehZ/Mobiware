package com.ktxdevelopment.mobiware.clients

import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputEditText
import com.ktxdevelopment.mobiware.R

object TextInputClient {

    fun validateSignInInput(etUsername: TextInputEditText, etPassword:TextInputEditText,etEmail : TextInputEditText) : Boolean {
        var correctInput = true
        if(!validateUsername(etUsername)) correctInput = false
        if(!validatePassword(etPassword)) correctInput = false
        if (!validateEmail(etEmail)) correctInput = false
        return correctInput
    }



    private fun validString(str: String) = str.matches("\\p{Alnum}*".toRegex())
    private fun validLong(str: String) = str.isDigitsOnly()
    fun validateFilledInput(str: String) = str.isNotEmpty()


    fun validateMobileNumber(et: TextInputEditText): Boolean {
        val number = et.text.toString()
        if (validLong(number)) return true
        return false
    }

    fun validateUsername (et: TextInputEditText): Boolean {
        val name = et.text.toString()
        return if (name.trim().length < 5) {
            et.error = et.context.getString(R.string.username_5_or_more_letters); false
        } else if (name.isDigitsOnly()) {
            et.error = et.context.getString(R.string.username_at_least_a_letter); false
        } else if (!validString(name)) {
            et.error = et.context.getString(R.string.no_special_symbols_allowed); false
        }else{
            true
        }
    }

    fun validateEmail(et: TextInputEditText): Boolean {
        val email = et.text.toString()
        return if (!validateFilledInput(email)) {
            et.error = "PLease enter a valid email"; false
        }else true
    }

    fun validatePassword(et: TextInputEditText): Boolean {
        val pass = et.text.toString()
        return if (pass.length <= 6) {
            et.error = et.context.getString(R.string.password_at_leats_6_chars) ;false
        } else if (!validString(pass)) {
            et.error = et.context.getString(R.string.no_special_symbols_allowed) ;false
        } else {
            true
        }
    }
}