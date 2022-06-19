package com.ktxdevelopment.mobiware.clients.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.work.Data;
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.postDelayed
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.databinding.ActivitySignUpBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import com.ktxdevelopment.mobiware.ui.activities.SignInActivity
import com.ktxdevelopment.mobiware.ui.activities.SignUpActivity
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.workers.RoomMobileWorker
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
            else -> {
                context.showErrorSnackbar(context.getString(R.string.smth_went_wrong)); binding.gifProgressSignUp.visibility =
                    GONE
            }
        }
    }

    fun handleErrorIn(context: SignInActivity, binding: ActivitySignInBinding, error: Exception?) {
        when (error) {
            is IOException -> Handler(Looper.getMainLooper()).postDelayed(1000) {
                context.searchAgainIfNoConnection()
            }
            else -> {
                context.showErrorSnackbar(context.getString(R.string.smth_went_wrong)); binding.gifProgressSignIn.visibility =
                    GONE
            }
        }
    }

    private fun phoneNotFoundLayoutVisible(binding: ViewBinding) {
        if (binding is ActivitySignUpBinding) {
            binding.llInsertPhoneManually.visibility = VISIBLE
            binding.gifProgressSignUp.visibility = GONE
        } else if (binding is ActivitySignInBinding) {
            binding.llInsertPhoneManually.visibility = VISIBLE
            binding.gifProgressSignIn.visibility = GONE
        }
    }

    fun phoneNotFoundLayoutDisappear(binding: ViewBinding) {
        if (binding is ActivitySignUpBinding) {
            binding.llInsertPhoneManually.visibility = GONE
            binding.gifProgressSignUp.visibility = VISIBLE
        } else if (binding is ActivitySignInBinding) {
            binding.llInsertPhoneManually.visibility = GONE
            binding.gifProgressSignIn.visibility = VISIBLE
        }
    }

    fun handleSearchSuccessUp(
        context: SignUpActivity,
        binding: ActivitySignUpBinding,
        res: Resource.Success<SearchResponse>
    ) {
        if (res.data != null) {
            if (res.data.data.phones.isNotEmpty()) {
                context.onSearchResponseResult(res.data)
            } else {
                phoneNotFoundLayoutVisible(binding)
            }
        } else {
            context.showErrorSnackbar(context.getString(R.string.smth_went_wrong))
            binding.gifProgressSignUp.visibility = GONE
        }
    }


    fun handleSearchSuccessIn(
        context: SignInActivity,
        binding: ActivitySignInBinding,
        res: Resource.Success<SearchResponse>
    ) {
        if (res.data != null) {
            if (res.data.data.phones.isNotEmpty()) {
                context.onSearchResponseResult(res.data)
            } else {
                phoneNotFoundLayoutVisible(binding)
            }
        } else {
            context.showErrorSnackbar(context.getString(R.string.smth_went_wrong))
            binding.gifProgressSignIn.visibility = GONE
        }
    }

    fun handleGetError(context: BaseActivity, error: Exception?) {
        when (error) {
            is IOException -> {
                context.showErrorSnackbar(context.getString(R.string.no_connection_error))
            }
            else -> {
                context.showErrorSnackbar(context.getString(R.string.smth_went_wrong))
            }
        }
    }
}
