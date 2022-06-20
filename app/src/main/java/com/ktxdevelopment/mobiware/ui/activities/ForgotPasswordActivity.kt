package com.ktxdevelopment.mobiware.ui.activities

import android.content.Intent
import android.os.Bundle
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.TextInputClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
    }

    private fun setUpUI() {
        binding.toolbarForgotPassword.setNavigationOnClickListener {
//            Intent(this, SignInActivity::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
//            }.also { startActivity(it); finish() }
            //todo check if activity finishes correctly
            finish()
        }

        binding.btnResetPassword.setOnClickListener {
            hideKeyboardInternal()
            if (TextInputClient.validateEmail(binding.etResetPasswordEmail)) {
                if (BaseClient.hasInternetConnection(this)) {
                    showProgressDialog()
                    FirebaseClient.resetPasswordWithEmail(
                        this,
                        binding.etResetPasswordEmail.text.toString()
                    )
                }else{ showErrorSnackbar(getString(R.string.no_connection_error)) }
            }
        }
    }

    fun onResetPasswordSuccess() {
        hideProgressDialog()
        showErrorSnackbar(getString(R.string.reset_email_has_sent))
    }

    fun onResetPasswordError(message: String) {
        hideProgressDialog()
        showErrorSnackbar(message)
    }


    private fun hideKeyboardInternal() = hideKeyboard(binding.etResetPasswordEmail)

}