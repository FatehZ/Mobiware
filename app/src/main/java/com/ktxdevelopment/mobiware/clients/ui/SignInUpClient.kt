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
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.util.tryEr
import java.io.IOException


object SignInUpClient {
     fun initializeRecyclerView(context: Context, binding: FragmentSignUpBinding, adapter: SelectionAdapter) {
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

     fun handleSearchSuccess(context: BaseFragment, bindingUp: FragmentSignUpBinding? = null, bindingIn: FragmentSignInBinding? = null, res: Resource.Success<SearchResponse>) {
          if (res.data?.data != null) {
               if (res.data.data.phones.isNotEmpty()) {
                    if (context is FragmentSignUp) context.onSearchResponseResult(res.data)
                    else if (context is FragmentSignIn) context.onSearchResponseResult(res.data)
               } else {
                    if (bindingUp == null) phoneNotFoundLayoutVisible(bindingIn!!)
                    else phoneNotFoundLayoutVisible(bindingUp)
               }
          } else {
               context.showErrorSnackbar(context.getString(R.string.smth_went_wrong))
               if (bindingUp == null) bindingIn!!.gifProgressSignIn.visibility = GONE
               else bindingUp.gifProgressSignUp.visibility = GONE
          }
     }

     fun handleSignError(
          context: BaseFragment,
          bindingIn: FragmentSignInBinding? = null,
          bindingUp: FragmentSignUpBinding? = null,
          error: Exception?
     ) {
          when (error) {
               is IOException -> Handler(Looper.getMainLooper()).postDelayed(1000) {
                    if (context is FragmentSignIn) context.searchAgainIfNoConnection()
                    else if (context is FragmentSignUp) context.searchAgainIfNoConnection()
               }
               else -> {
                    context.showErrorSnackbar(context.getString(R.string.smth_went_wrong))
                    if (bindingUp == null) bindingIn!!.gifProgressSignIn.visibility = GONE
                    else bindingUp.gifProgressSignUp.visibility = GONE
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
