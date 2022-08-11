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
import com.ktxdevelopment.mobiware.clients.main.BaseClient
import com.ktxdevelopment.mobiware.clients.main.TextInputClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
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
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentSignUp : BaseFragment(), SelectionAdapter.OnMobileClickListener {

     private lateinit var binding: FragmentSignUpBinding

     private lateinit var restViewModel: RetroViewModel
     private val TAG = "SIGN_IN_TAG"
     private lateinit var mobileAdapter: SelectionAdapter

     private var phones: ArrayList<Phone> = ArrayList()
     private var selectedPhoneUrl: String = ""


     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentSignUpBinding.inflate(inflater, container, false)
          setUpSignInUI()

          if (arguments?.getParcelableArrayList<Phone>(Constants.PHONE_LIST) != null) {
               phones = arguments?.getParcelableArrayList(Constants.PHONE_LIST)!!
               onPhonesObtainedFromIntent()
          }else{
               restViewModel.searchMobile(BaseClient.getDeviceModel())
               restViewModel.searchResponse.observe(viewLifecycleOwner) { res ->
                    when (res) {
                         is Resource.Success -> {
                              binding.gifProgressSignUp.visibility = View.GONE
                              SignInUpClient.handleSearchSuccessUp(this, binding, res)
                         }
                         is Resource.Error -> { SignInUpClient.handleErrorUp(this, binding, res.error) }
                         else -> Unit
                    }
               }
          }

          binding.etUsernameSignIn.filters = arrayOf(InputFilter { cs, _, _, _, _, _ ->
               if (cs == "") { return@InputFilter cs }
               if (cs.toString().matches(("[a-zA-Z ]+").toRegex())) { cs } else ""
          })

          return binding.root
     }






     fun searchAgainIfNoConnection() { restViewModel.searchMobile(BaseClient.getDeviceModel()) }


     private fun signButtonClickListener() {
          hideKeyboardInternal()
          if (TextInputClient.validateSignUpInput(binding.etUsernameSignIn, binding.etPasswordSignIn, binding.etEmailSignIn)) {
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
          val roomViewModel = (activity as IntroductionActivity).getLocalViewModel()

          restViewModel.getResponse.observe(this) { res ->
               when (res) {
                    is Resource.Success -> {
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
                    is Resource.Error -> {
                         (activity as BaseActivity).hideProgressDialog()
                         Firebase.auth.signOut()
                         SignInUpClient.handleGetError(activity as IntroductionActivity, res.error)
                    }
                    else -> Unit
               }
          }
     }


     private fun launchRegistration() {
          showProgressDialog()
          restViewModel.getMobile(selectedPhoneUrl)
          FirebaseClient.registerUserAuth(
               this,
               binding.etUsernameSignIn.text!! ,
               binding.etEmailSignIn.text!!,
               binding.etPasswordSignIn.text!!,
               selectedPhoneUrl
          )
     }

     private fun onPhonesObtainedFromIntent() {
          binding.gifProgressSignUp.visibility = View.GONE
          mobileAdapter = SelectionAdapter(this)
          SignInUpClient.initializeRecyclerView(requireContext(), binding, mobileAdapter)
          binding.tvSignPhoneModel.visibility = View.VISIBLE
          mobileAdapter.setData(phones)
     }

     fun onSearchResponseResult(response: SearchResponse) {
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
          restViewModel = (activity as IntroductionActivity).getRetroViewModel()
          restViewModel.searchMobile(BaseClient.getDeviceModel())
          binding.btnSignUp.setOnClickListener { signButtonClickListener() }
          binding.btnSubmitPhoneModel.setOnClickListener { submitPhoneSearchAgain(binding.etMobileInsertManually) }
          binding.btnHaveAccountSignIn.setOnClickListener { launchSignInIntent() }
     }

     private fun submitPhoneSearchAgain(et: TextInputEditText) {
          if (TextInputClient.validatePhoneModel(et)){
               restViewModel.searchMobile(et.text.toString())
               SignInUpClient.phoneNotFoundLayoutDisappear(binding)
          }
     }


     private fun hideKeyboardInternal() = (activity as BaseActivity).hideKeyboard(binding.etEmailSignIn, binding.etPasswordSignIn, binding.etUsernameSignIn, binding.etMobileInsertManually)
}