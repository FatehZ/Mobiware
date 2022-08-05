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
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.TextInputClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
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
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentSignIn : BaseFragment(), SelectionAdapter.OnMobileClickListener {

     private lateinit var binding: FragmentSignInBinding
     private lateinit var restViewModel: RetroViewModel
     private val TAG = "SIGN_IN_TAG"
     private lateinit var mobileAdapter: SelectionAdapter

     private var phones: ArrayList<Phone> = ArrayList()
     private var selectedPhoneUrl: String = ""


     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentSignInBinding.inflate(inflater, container, false)
          setupSignInUI()


          if (arguments?.getParcelableArrayList<Phone>(Constants.PHONE_LIST) != null) {
               phones = arguments?.getParcelableArrayList(Constants.PHONE_LIST)!!
               onPhonesObtainedFromIntentIn()
          }else{
               restViewModel.searchResponse.observe(requireActivity()) { res ->
                    when (res) {
                         is Resource.Success -> {
                              binding.gifProgressSignIn.visibility = View.GONE
                              SignInUpClient.handleSearchSuccessIn(this, binding, res)
                         }
                         is Resource.Error -> {
                              SignInUpClient.handleErrorIn(this,binding, res.error)
                         }
                         else -> Unit
                    }
               }
          }

          return binding.root
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
          val roomViewModel = (activity as IntroductionActivity).getLocalViewModel()

          restViewModel.getResponse.observe(this) { res ->
               when (res) {
                    is Resource.Success -> {
                         hideProgressDialog()
                         roomViewModel.writeUserToPreferences(requireContext(), updatedUser, selectedPhoneUrl)
                         roomViewModel.writeMobileToRoomDB(requireContext(), res.data!!.data, selectedPhoneUrl)
//                         roomViewModel.writeMobileToFirestore(requireContext(), res.data.data)

                         Intent(requireActivity(), MainActivity::class.java).apply {
                              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                              putExtra(Constants.PHONE_EXTRA, RoomPhoneModel(selectedPhoneUrl, res.data.data))
                              putExtra(Constants.USER_EXTRA, updatedUser)
                         }.also { startActivity(it); activity?.finish() }

                    }
                    is Resource.Error -> {
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
          restViewModel.getMobile(selectedPhoneUrl)

          FirebaseClient.signInUserAuth(this,
               binding.etEmailSignIn.text!!,
               binding.etPasswordSignIn.text!!,
               selectedPhoneUrl
          )
     }


     private fun onPhonesObtainedFromIntentIn() {
          binding.gifProgressSignIn.visibility = View.GONE
          mobileAdapter = SelectionAdapter(this)
          SignInUpClient.initializeRecyclerViewIn(requireContext(), binding, mobileAdapter)
          binding.cvSignUselessIn.visibility = View.VISIBLE
          mobileAdapter.setData(phones)
     }

     fun onSearchResponseResult(response: SearchResponse) {
          phones = BaseClient.whichModelSuits(response.data.phones) as ArrayList<Phone>
          mobileAdapter = SelectionAdapter(this)
          SignInUpClient.initializeRecyclerViewIn(requireContext(), binding, mobileAdapter)
          binding.cvSignUselessIn.visibility = View.VISIBLE
          mobileAdapter.setData(phones)
     }

     private fun launchSignUpIntent() {
          if (phones.isNotEmpty()) {
               val bundle = Bundle().apply { putParcelableArrayList(Constants.PHONE_LIST, phones) }
               findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentSignUp, bundle)
          }else{
               findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentSignUp)
          }
     }


     fun searchAgainIfNoConnection() = restViewModel.searchMobile(BaseClient.getDeviceModel())


     private fun setupSignInUI() {
          restViewModel = (activity as IntroductionActivity).getRetroViewModel()
          restViewModel.searchMobile(BaseClient.getDeviceModel())
          binding.btnSignIn.setOnClickListener { signButtonClickListener() }
          binding.btnSubmitPhoneModelIn.setOnClickListener { submitPhoneSearchAgain(binding.etMobileInsertManuallyIn) }
          binding.btnNoAccountSignUp.setOnClickListener { launchSignUpIntent() }
          binding.btnForgotPasswordSignIn.setOnClickListener { launchForgotPassword() }
     }

     private fun launchForgotPassword() {
          findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentForgotPassword)
     }


     private fun submitPhoneSearchAgain(et: TextInputEditText) {
          if (TextInputClient.validatePhoneModel(et)){
               restViewModel.searchMobile(et.text.toString())
               SignInUpClient.phoneNotFoundLayoutDisappear(binding)
          }
     }

     private fun hideKeyboardInternal() = (activity as IntroductionActivity).hideKeyboard(binding.etEmailSignIn, binding.etPasswordSignIn, binding.etMobileInsertManuallyIn)
}