package com.ktxdevelopment.mobiware.clients

import android.util.Patterns
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputEditText
import com.ktxdevelopment.mobiware.R

object TextInputClient {

    fun validateSignInInput(editText: TextInputEditText, password:TextInputEditText, name: String) : Boolean {
        if (name.isBlank()) {
            editText.error = editText.context.getString(R.string.blank_username)
            return false
        }else {
            //TODO
            // validate no symbols
            return true
        }
    }



    fun validString(str: String) = str.matches("\\p{Alnum}*".toRegex())
    fun validLong(str: String) = str.isDigitsOnly()

    fun validateMobileNumber(number: String, et: TextInputEditText): Boolean {
        if (!validateEmptyInput(number)) return true
        return true
    }

    fun validateUsername(name: String, et: TextInputEditText): Boolean {
        if (validString(name))
        return true
    }

    fun validateEmail(email: String, et: TextInputEditText): Boolean {
        return if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et.error = "PLease enter a valid email"; true
        }else false
    }

    fun validateEmptyInput(str: String): Boolean {
        return str.isNotEmpty()
    }
}