package com.ktxdevelopment.mobiware.clients

import android.util.Patterns
import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputEditText
import com.ktxdevelopment.mobiware.R


object TextInputClient {

    fun validateSignUpInput(etUsername: TextInputEditText, etPassword:TextInputEditText, etEmail : TextInputEditText) : Boolean {
        var correctInput = true
        if(!validateUsername(etUsername)) correctInput = false
        if(!validatePassword(etPassword)) correctInput = false
        if (!validateEmail(etEmail)) correctInput = false
        return correctInput
    }

    fun validateSignInInput( etPassword:TextInputEditText, etEmail : TextInputEditText) : Boolean {
        var correctInput = true
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
            et.error = et.context.getString(R.string.at_least_a_letter); false
        } else if (!validString(name)) {
            et.error = et.context.getString(R.string.no_special_symbols_allowed); false
        }else{
            true
        }
    }

    fun validateEmail(et: TextInputEditText): Boolean {
        val email = et.text.toString().trim { it <= ' ' }
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return if (!email.matches(emailPattern.toRegex())) {
            et.error = "Enter a valid email"; false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et.error = "Enter a valid email"; false
        }
        else true
    }

    private fun validatePassword(et: TextInputEditText): Boolean {
        val pass = et.text.toString()
        return if (pass.length < 6) {
            et.error = et.context.getString(R.string.password_at_least_6_chars) ;false
        } else if (!validString(pass)) {
            et.error = et.context.getString(R.string.no_special_symbols_allowed);false
        }else if (pass.isDigitsOnly()) {
            et.error = et.context.getString(R.string.at_least_a_letter) ;false
        } else {
            true
        }
    }

    fun validatePhoneModel(et: TextInputEditText): Boolean {
        return if (!validateFilledInput(et.text.toString())) {
            et.error = et.context.getString(R.string.no_empty_input); false
        }else if (et.text.toString().trim().length < 4){
            et.error = et.context.getString(R.string.too_short_enter_valid); false
        }else true
    }
}