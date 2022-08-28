package com.ktxdevelopment.mobiware.ui.fragments.intro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.main.BaseClient
import com.ktxdevelopment.mobiware.clients.main.TextInputClient
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient
import com.ktxdevelopment.mobiware.databinding.FragmentSignInBinding
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import com.ktxdevelopment.mobiware.ui.activities.IntroductionActivity
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.tryEr
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSignIn : BaseFragment(), SelectionAdapter.OnMobileClickListener {

     private lateinit var binding: FragmentSignInBinding
     private lateinit var mobileAdapter: SelectionAdapter

     private var phones: ArrayList<Phone> = ArrayList()
     private var selectedPhoneUrl: String = ""


     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentSignInBinding.inflate(inflater, container, false)
          return binding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          setupSignInUI()

          tryEr {
               introActivity.retroViewModel.searchResponse.observe(requireActivity()) { res ->
                    when (res) {
                         is Resource.Success -> {
                              SignInUpClient.handleSearchSuccess(context = this, bindingIn = binding, res = res)
                         }
                         is Resource.Error -> {
                              SignInUpClient.handleSignError(context = this, bindingIn =  binding, error = res.error)
                         }
                         else -> Unit
                    }
               }
          }
     }


     private fun signButtonClickListener() {
          hideKeyboardInternal()
          if (TextInputClient.validateSignInInput(binding.etPasswordSignIn, binding.etEmailSignIn)) {
               if (BaseClient.hasInternetConnection(requireContext())) {
                    if(selectedPhoneUrl != "") {
                         launchSignIn()
                    }else (activity as BaseActivity).showErrorSnackbar(getString(R.string.select_a_phone_error))
               } else (activity as BaseActivity).showErrorSnackbar(getString(R.string.no_connection_error))
          }
     }

     override fun onMobileClick(position: Int) {
          selectedPhoneUrl = phones[position].slug
          if (binding.tvSignPhoneModelIn.visibility == View.GONE) binding.tvSignPhoneModelIn.visibility = View.VISIBLE
          binding.tvSignPhoneModelIn.text = phones[position].phone_name
     }

     fun onSignInSuccess(updatedUser: LocalUser) {
          val roomViewModel = introActivity.localViewModel

          introActivity.retroViewModel.getResponse.observe(this) { res ->
               when (res) {
                    is Resource.Success<*> -> {
                         hideProgressDialog()
                         roomViewModel.writeUserToPreferences(requireContext(), updatedUser, selectedPhoneUrl)
                         roomViewModel.writeMobileToRoomDB(requireContext(), res.data!!.data, selectedPhoneUrl)

                         Intent(requireActivity(), MainActivity::class.java).apply {
                              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                              putExtra(Constants.PHONE_EXTRA, RoomPhoneModel(selectedPhoneUrl, res.data.data))
                              putExtra(Constants.USER_EXTRA, updatedUser)
                         }.also { startActivity(it); activity?.finish() }
                    }
                    is Resource.Error<*> -> {
                         hideProgressDialog()
                         Firebase.auth.signOut()
                         SignInUpClient.handleGetError((activity as IntroductionActivity), res.error)
                    }
                    else -> Unit
               }
          }
     }


     private fun launchSignIn() {
          showProgressDialog()
          introActivity.retroViewModel.getMobile(selectedPhoneUrl)

          FirebaseClient.signInUserAuth(this,
               binding.etEmailSignIn.text!!,
               binding.etPasswordSignIn.text!!,
               selectedPhoneUrl
          )
     }


     fun onSearchResponseResult(response: SearchResponse) {
          binding.gifProgressSignIn.visibility = View.GONE
          phones = BaseClient.whichModelSuits(response.data.phones) as ArrayList<Phone>
          mobileAdapter = SelectionAdapter(this)
          SignInUpClient.initializeRecyclerViewIn(requireContext(), binding, mobileAdapter)
          binding.cvSignUselessIn.visibility = View.VISIBLE
          mobileAdapter.setData(phones)
     }

     private fun launchSignUpIntent() { findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentSignUp) }


     private fun setupSignInUI() {
          tryEr {
               binding.btnSignIn.setOnClickListener { signButtonClickListener() }
               binding.btnSubmitPhoneModelIn.setOnClickListener { submitPhoneSearchAgain(binding.etMobileInsertManuallyIn) }
               binding.btnNoAccountSignUp.setOnClickListener { launchSignUpIntent() }
               binding.btnForgotPasswordSignIn.setOnClickListener { launchForgotPassword() }
          }


     }

     private fun launchForgotPassword() {
          findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentForgotPassword)
     }


     private fun submitPhoneSearchAgain(et: TextInputEditText) {
          if (TextInputClient.validatePhoneModel(et)){
               introActivity.retroViewModel.searchMobiles(et.text.toString())
               SignInUpClient.phoneNotFoundLayoutDisappear(binding)
          }
     }

     private fun hideKeyboardInternal() = (activity as IntroductionActivity).hideKeyboard(binding.etEmailSignIn, binding.etPasswordSignIn, binding.etMobileInsertManuallyIn)

     private val introActivity by lazy { activity as IntroductionActivity }

     fun searchAgainIfNoConnection() = introActivity.retroViewModel.searchMobiles(BaseClient.getDeviceModel())

}