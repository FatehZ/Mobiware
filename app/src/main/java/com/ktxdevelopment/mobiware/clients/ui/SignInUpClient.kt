package com.ktxdevelopment.mobiware.clients.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.exceptions.RequestBodyEmptyException
import com.ktxdevelopment.mobiware.clients.exceptions.RequestUnsuccessfulException
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.databinding.ActivitySignUpBinding
import com.ktxdevelopment.mobiware.ui.activities.SignInActivity
import com.ktxdevelopment.mobiware.ui.activities.SignUpActivity
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import java.io.IOException

object SignInUpClient {
    fun initializeRecyclerView(
        context: Context,
        binding: ActivitySignUpBinding,
        adapter: SelectionAdapter
    ) {
        val lManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        lManager.stackFromEnd = true
        binding.rvSelectionMobiles.layoutManager = lManager

        binding.rvSelectionMobiles.adapter = adapter
        binding.tvSignPhoneModel.visibility = VISIBLE
    }


    fun initializeRecyclerViewIn(
        context: Context,
        binding: ActivitySignInBinding,
        adapter: SelectionAdapter
    ) {
        val lManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        lManager.stackFromEnd = true
        binding.rvSelectionMobilesIn.layoutManager = lManager

        binding.rvSelectionMobilesIn.adapter = adapter
        binding.tvSignPhoneModelIn.visibility = VISIBLE
    }



    fun handleErrorUp(context: SignUpActivity, binding: ActivitySignUpBinding, error: Exception?) {
        when (error) {
            is IOException -> Handler(Looper.getMainLooper()).postDelayed(1000) {
                context.searchAgainIfNoConnection()
            }
            is RequestBodyEmptyException -> phoneNotFoundLayoutVisible(binding)
            is RequestUnsuccessfulException -> context.showErrorSnackbar(context.getString(R.string.smth_went_wrong))
        }
    }

    fun handleErrorIn(context: SignInActivity, binding: ActivitySignInBinding, error: Exception?) {
        when (error) {
            is IOException -> Handler(Looper.getMainLooper()).postDelayed(1000) {
                context.searchAgainIfNoConnection()
            }
            is RequestBodyEmptyException -> phoneNotFoundLayoutVisible(binding)
            is RequestUnsuccessfulException -> context.showErrorSnackbar(context.getString(R.string.smth_went_wrong))
        }
    }

    private fun phoneNotFoundLayoutVisible(binding: ViewBinding) {
        if (binding is ActivitySignUpBinding) {
            binding.llInsertPhoneManually.visibility = VISIBLE
            binding.gifProgressSignUp.visibility = GONE
        }
        else if (binding is ActivitySignInBinding) {
            binding.llInsertPhoneManually.visibility = VISIBLE
            binding.gifProgressSignIn.visibility = GONE
        }
    }

    fun phoneNotFoundLayoutDisappear(binding: ViewBinding) {
        if (binding is ActivitySignUpBinding) {
            binding.llInsertPhoneManually.visibility = GONE
            binding.gifProgressSignUp.visibility = VISIBLE
        }
        else if (binding is ActivitySignInBinding) {
            binding.llInsertPhoneManually.visibility = GONE
            binding.gifProgressSignIn.visibility = VISIBLE
        }
    }
}
