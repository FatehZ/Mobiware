package com.ktxdevelopment.mobiware.ui.fragments.intro

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
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
import com.ktxdevelopment.mobiware.databinding.FragmentSignUpBinding
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
class FragmentSignUp : BaseFragment(), SelectionAdapter.OnMobileClickListener {

     private lateinit var binding: FragmentSignUpBinding

     private lateinit var mobileAdapter: SelectionAdapter

     private var phones: ArrayList<Phone> = ArrayList()
     private var selectedPhoneUrl: String = ""


     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentSignUpBinding.inflate(inflater, container, false)
          setUpSignInUI()



          introActivity.retroViewModel.searchResponse.observe(requireActivity()) { res ->
               when (res) {
                    is Resource.Success -> {
                         SignInUpClient.handleSearchSuccess(context = this, bindingUp = binding, res =  res)
                    }
                    is Resource.Error -> {
                         SignInUpClient.handleSignError(context = this, bindingUp =  binding, error = res.error)
                    }
                    else -> Unit
               }
          }


          binding.etUsernameSignUp.filters = arrayOf(InputFilter { cs, _, _, _, _, _ ->
               if (cs == "") { return@InputFilter cs }
               if (cs.toString().matches(("[0-9a-zA-Z ]+").toRegex())) { cs } else ""
          },
               InputFilter.LengthFilter(20)
          )

          return binding.root
     }



     private fun signButtonClickListener() {
          hideKeyboardInternal()
          if (TextInputClient.validateSignUpInput(binding.etUsernameSignUp, binding.etPasswordSignIn, binding.etEmailSignIn)) {
               if (BaseClient.hasInternetConnection(requireContext())) {
                    if(selectedPhoneUrl != "") {
                         launchRegistration()
                    }else (activity as BaseActivity).showErrorSnackbar(getString(R.string.select_a_phone_error))
               } else (activity as BaseActivity).showErrorSnackbar(getString(R.string.no_connection_error))
          }
     }

     override fun onMobileClick(position: Int) {
          selectedPhoneUrl = phones[position].slug
          if (binding.tvSignPhoneModel.visibility == View.GONE) binding.tvSignPhoneModel.visibility = View.VISIBLE
          binding.tvSignPhoneModel.text = phones[position].phone_name
     }

     fun onRegisterSuccess(user: LocalUser) {
          val roomViewModel = introActivity.localViewModel

          introActivity.retroViewModel.getResponse.observe(this) { res ->
               when (res) {
                    is Resource.Success<*> -> {
                         (activity as BaseActivity).hideProgressDialog()
                         roomViewModel.writeUserToPreferences(requireContext(), user, selectedPhoneUrl)
                         roomViewModel.writeMobileToRoomDB(requireContext(), res.data!!.data, selectedPhoneUrl)
//                         roomViewModel.writeMobileToFirestore(requireContext(), res.data.data)

                         Intent(requireActivity(), MainActivity::class.java).apply {
                              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                              putExtra(Constants.PHONE_EXTRA, RoomPhoneModel(selectedPhoneUrl, res.data.data))
                              putExtra(Constants.USER_EXTRA, BaseClient.convertFireToLocalUser(user))
                         }.also { startActivity(it); activity?.finish() }

                    }
                    is Resource.Error<*> -> {
                         introActivity.hideProgressDialog()
                         Firebase.auth.signOut()
                         SignInUpClient.handleGetError(activity as IntroductionActivity, res.error)
                    }
                    else -> Unit
               }
          }
     }


     private fun launchRegistration() {
          showProgressDialog()
          introActivity.retroViewModel.getMobile(selectedPhoneUrl)
          FirebaseClient.registerUserAuth(
               this,
               binding.etUsernameSignUp.text!! ,
               binding.etEmailSignIn.text!!,
               binding.etPasswordSignIn.text!!,
               selectedPhoneUrl
          )
     }


     fun onSearchResponseResult(response: SearchResponse) {
          binding.gifProgressSignUp.visibility = View.GONE
          phones = BaseClient.whichModelSuits(response.data.phones) as ArrayList<Phone>
          mobileAdapter = SelectionAdapter(this)
          SignInUpClient.initializeRecyclerView(requireContext(), binding, mobileAdapter)
          binding.tvSignPhoneModel.visibility = View.VISIBLE
          mobileAdapter.setData(phones)
     }

     private fun launchSignInIntent() {
          if (phones.isNotEmpty()) {
               val bundle = Bundle().apply { putParcelableArrayList(Constants.PHONE_LIST, phones) }
               findNavController().navigate(R.id.action_fragmentSignUp_to_fragmentSignIn, bundle)
          } else {
               findNavController().navigate(R.id.action_fragmentSignUp_to_fragmentSignIn)
          }
     }


     private fun setUpSignInUI() {
          binding.btnSignUp.setOnClickListener { signButtonClickListener() }
          binding.btnSubmitPhoneModel.setOnClickListener { submitPhoneSearchAgain(binding.etMobileInsertManually) }
          binding.btnHaveAccountSignIn.setOnClickListener { launchSignInIntent() }
     }

     private fun submitPhoneSearchAgain(et: TextInputEditText) {
          if (TextInputClient.validatePhoneModel(et)){
               introActivity.retroViewModel.searchMobiles(et.text.toString())
               SignInUpClient.phoneNotFoundLayoutDisappear(binding)
          }
     }


     private fun hideKeyboardInternal() = tryEr { introActivity.hideKeyboard(binding.etEmailSignIn, binding.etPasswordSignIn, binding.etUsernameSignUp, binding.etMobileInsertManually) }

     private val introActivity by lazy { activity as IntroductionActivity }

     fun searchAgainIfNoConnection() = introActivity.retroViewModel.searchMobiles(BaseClient.getDeviceModel())
}

