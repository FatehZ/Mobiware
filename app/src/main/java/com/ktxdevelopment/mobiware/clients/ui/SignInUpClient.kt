package com.ktxdevelopment.mobiware.clients.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.FragmentSignInBinding
import com.ktxdevelopment.mobiware.databinding.FragmentSignUpBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentSignIn
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentSignUp
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.util.tryEr
import java.io.IOException


object SignInUpClient {
        fun initializeRecyclerView(
                context: Context,
                binding: FragmentSignUpBinding,
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
                binding: FragmentSignInBinding,
                adapter: SelectionAdapter
        ) {
                val lManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                lManager.stackFromEnd = true
                binding.rvSelectionMobilesIn.layoutManager = lManager

                binding.rvSelectionMobilesIn.adapter = adapter
                binding.tvSignPhoneModelIn.visibility = VISIBLE
        }


        fun handleErrorUp(
                context: FragmentSignUp,
                binding: FragmentSignUpBinding,
                error: Exception?
        ) {
                when (error) {
                        is IOException -> Handler(Looper.getMainLooper()).postDelayed(1000) {
                                context.searchAgainIfNoConnection()
                        }
                        else -> {
                                (context.activity as BaseActivity).showErrorSnackbar(
                                        context.getString(
                                                R.string.smth_went_wrong
                                        )
                                )
                                binding.gifProgressSignUp.visibility = GONE
                        }
                }
        }

        fun handleErrorIn(
                context: FragmentSignIn,
                binding: FragmentSignInBinding,
                error: Exception?
        ) {
                when (error) {
                        is IOException -> Handler(Looper.getMainLooper()).postDelayed(1000) {
                                context.searchAgainIfNoConnection()
                        }
                        else -> {
                                (context.activity as BaseActivity).showErrorSnackbar(
                                        context.getString(
                                                R.string.smth_went_wrong
                                        )
                                ); binding.gifProgressSignIn.visibility = GONE
                        }
                }
        }

        private fun phoneNotFoundLayoutVisible(binding: ViewBinding) {
                if (binding is FragmentSignUpBinding) {
                        binding.llInsertPhoneManually.visibility = VISIBLE
                        binding.gifProgressSignUp.visibility = GONE
                } else if (binding is FragmentSignInBinding) {
                        binding.llInsertPhoneManually.visibility = VISIBLE
                        binding.gifProgressSignIn.visibility = GONE
                }
        }

        fun phoneNotFoundLayoutDisappear(binding: ViewBinding) {
                if (binding is FragmentSignUpBinding) {
                        binding.llInsertPhoneManually.visibility = GONE
                        binding.gifProgressSignUp.visibility = VISIBLE
                } else if (binding is FragmentSignInBinding) {
                        binding.llInsertPhoneManually.visibility = GONE
                        binding.gifProgressSignIn.visibility = VISIBLE
                }
        }

        fun handleSearchSuccessUp(context: FragmentSignUp, binding: FragmentSignUpBinding, res: Resource.Success<SearchResponse>) {
                if (res.data != null) {
                        if (res.data.data.phones.isNotEmpty()) {
                                context.onSearchResponseResult(res.data)
                        } else { phoneNotFoundLayoutVisible(binding) }
                } else {
                        (context.activity as BaseActivity).showErrorSnackbar(context.getString(R.string.smth_went_wrong))
                        binding.gifProgressSignUp.visibility = GONE
                }
        }


        fun handleSearchSuccessIn(
                context: FragmentSignIn,
                binding: FragmentSignInBinding,
                res: Resource.Success<SearchResponse>
        ) {
                tryEr {
                        if (res.data != null) {
                                if (res.data.data.phones.isNotEmpty()) {
                                        context.onSearchResponseResult(res.data)
                                } else {
                                        phoneNotFoundLayoutVisible(binding)
                                }
                        } else {
                                (context.activity as BaseActivity).showErrorSnackbar(
                                        context.getString(
                                                R.string.smth_went_wrong
                                        )
                                )
                                binding.gifProgressSignIn.visibility = GONE
                        }
                }
        }

        fun handleGetError(context: BaseActivity, error: Exception?) {
                when (error) {
                        is IOException -> {
                                tryEr { context.showErrorSnackbar(context.getString(R.string.no_connection_error)) }
                        }
                        else -> {
                                tryEr { context.showErrorSnackbar(context.getString(R.string.smth_went_wrong)) }
                        }
                }
        }
}
