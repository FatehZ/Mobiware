package com.ktxdevelopment.mobiware.clients.main

import android.util.Patterns
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ktxdevelopment.mobiware.R


object TextInputClient {

     fun validateSignUpInput(etUsername: TextInputEditText, etPassword:TextInputEditText,etEmail : TextInputEditText) : Boolean {
          var correctInput = true
          if(!validateUsername(etUsername)) correctInput = false
          if(!validatePassword(etPassword)) correctInput = false
          if (!validateEmail(etEmail)) correctInput = false
          return correctInput
     }

     fun validateSignInInput(etPassword: TextInputEditText, etEmail: TextInputEditText) : Boolean {
          var correctInput = true
          if(!validatePassword(etPassword))  correctInput = false
          if (!validateEmail(etEmail)) correctInput = false
          return correctInput
     }



     private fun validString(str: String) = str.matches("\\p{Alnum}*".toRegex())
     private fun validUsernameString(str: String) = str.matches("[0-9a-zA-Z ]+".toRegex())
     private fun validLong(str: String) = str.isDigitsOnly()
     fun validateFilledInput(str: String) = str.isNotEmpty()


     fun validateMobileNumber(et: TextInputEditText): Boolean {
          val number = et.text.toString()
          if (!validLong(number)) {
               et.error = et.context.getString(R.string.only_numbers)
               return false
          }else if(number.length < 9) {
               et.error = et.context.getString(R.string.error_mobile_short)
               return false
          }
          getParentTilOf(et).error = ""
          return true
     }

     fun validateUsername (et: TextInputEditText): Boolean {
          val name = et.text.toString()
          return if (name.trim().length < 5) {
               getParentTilOf(et).error = et.context.getString(R.string.username_5_or_more_letters); false
          } else if (name.isDigitsOnly()) {
               getParentTilOf(et).error = et.context.getString(R.string.at_least_a_letter); false
          } else if (!validUsernameString(name)) {
               getParentTilOf(et).error = et.context.getString(R.string.no_special_symbols_allowed); false
          }else{
               getParentTilOf(et).error = ""
               true
          }
     }

     fun validateEmail(et: TextInputEditText): Boolean {
          val email = et.text.toString().trim { it <= ' ' }
          val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

          return if (!email.matches(emailPattern.toRegex()) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
               getParentTilOf(et).error = "Enter a valid email"; false
          }else if (email.contains(" ")) {
               getParentTilOf(et).error = et.context.getString(R.string.no_free_spaces_allowed);false
          }
          else{
               getParentTilOf(et).error = ""
               true
          }
     }

     private fun validatePassword(et: TextInputEditText): Boolean {
          val pass = et.text.toString()
          return if (pass.length < 6) {
               getParentTilOf(et).error =
                    et.context.getString(R.string.password_at_least_6_chars);false
          }else if (pass.contains(" ")) {
               getParentTilOf(et).error = et.context.getString(R.string.no_free_spaces_allowed);false
          } else if (!validString(pass)) {
               getParentTilOf(et).error = et.context.getString(R.string.no_special_symbols_allowed);false
          }else if (pass.isDigitsOnly()) {
               getParentTilOf(et).error = et.context.getString(R.string.at_least_a_letter) ;false
          } else {
               getParentTilOf(et).error = ""
               true
          }
     }

     fun validatePhoneModel(et: TextInputEditText): Boolean {
          return if (!validateFilledInput(et.text.toString())) {
               et.error = et.context.getString(R.string.no_empty_input); false
          }else if (et.text.toString().trim().length < 4){
               et.error = et.context.getString(R.string.too_short_enter_valid); false
          }else {
               getParentTilOf(et).error = ""
               true
          }
     }


     private fun getParentTilOf(et: EditText): TextInputLayout {
          return (et.parent.parent as TextInputLayout)
     }
}