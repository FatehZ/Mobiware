package com.ktxdevelopment.mobiware.ui.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.TextInputClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.FragmentForgotPasswordBinding
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import com.ktxdevelopment.mobiware.ui.activities.IntroductionActivity
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment


class FragmentForgotPassword : BaseFragment() {

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        setUpUI()
        return binding.root
    }

    private fun setUpUI() {
        binding.toolbarForgotPassword.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_fragmentForgotPassword_to_fragmentSignIn)
        }

        binding.btnResetPassword.setOnClickListener {
            hideKeyboardInternal()
            if (TextInputClient.validateEmail(binding.etResetPasswordEmail)) {
                if (BaseClient.hasInternetConnection(requireContext())) {
                    (activity as IntroductionActivity).showProgressDialogCancellable()
                    FirebaseClient.resetPasswordWithEmail(
                        this,
                        binding.etResetPasswordEmail.text.toString()
                    )
                } else {
                    (activity as BaseActivity).showErrorSnackbar(getString(R.string.no_connection_error))
                }
            }
        }
    }

    fun onResetPasswordSuccess() {
        hideProgressDialog()
        binding.etResetPasswordEmail.setText("")
        showSuccessSnackbar(getString(R.string.reset_email_has_sent))
    }

    fun onResetPasswordError(message: String) {
        hideProgressDialog()
        showErrorSnackbar(message)
    }


    private fun hideKeyboardInternal() = (activity as BaseActivity).hideKeyboard(binding.etResetPasswordEmail)
}