package com.ktxdevelopment.mobiware.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.BaseClient.whichModelSuits
import com.ktxdevelopment.mobiware.clients.Preferences.saveUserDetailsToPreferences
import com.ktxdevelopment.mobiware.clients.TextInputClient
import com.ktxdevelopment.mobiware.clients.TextInputClient.validateSignUpInput
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.initializeRecyclerView
import com.ktxdevelopment.mobiware.databinding.ActivitySignUpBinding
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter.OnMobileClickListener
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignUpActivity : BaseActivity(), OnMobileClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var restViewModel: RetroViewModel
    private val TAG = "SIGN_IN_TAG"
    private lateinit var mobileAdapter: SelectionAdapter

    private var phones: ArrayList<Phone> = ArrayList()
    private var selectedPhoneUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpSignInUI()


        if (intent.hasExtra(Constants.PHONE_LIST)) {
            phones = intent.getParcelableArrayListExtra(Constants.PHONE_LIST)!!
            onPhonesObtainedFromIntent()
        } else {
            restViewModel.searchMobile(BaseClient.getDeviceModel())
            restViewModel.searchResponse.observe(this) { res ->
                when (res) {
                    is Resource.Success -> {
                        binding.gifProgressSignUp.visibility = GONE
                        SignInUpClient.handleSearchSuccessUp(this, binding, res)
                    }
                    is Resource.Error -> {
                        SignInUpClient.handleErrorUp(this, binding, res.error)
                    }
                    else -> Unit
                }
            }
        }
    }


    fun searchAgainIfNoConnection() { restViewModel.searchMobile(BaseClient.getDeviceModel()) }


    private fun signButtonClickListener() {
        hideKeyboardInternal()
        if (validateSignUpInput(binding.etUsernameSignIn, binding.etPasswordSignIn , binding.etEmailSignIn)) {
            if (hasInternetConnection(this)) {
                if(selectedPhoneUrl != "") {
                    launchRegistration()
                }else showErrorSnackbar(getString(R.string.select_a_phone_error))
            } else showErrorSnackbar(getString(R.string.no_connection_error))
        }
    }


    override fun onMobileClick(position: Int) {
        selectedPhoneUrl = phones[position].detail
        if (binding.tvSignPhoneModel.visibility == GONE) binding.tvSignPhoneModel.visibility = VISIBLE
        binding.tvSignPhoneModel.text = phones[position].phone_name
    }

    fun onRegisterSuccess(user: FireUser) {

        lifecycleScope.launch { saveUserDetailsToPreferences(this@SignUpActivity,user) }

        restViewModel.getResponse.observe(this) { res ->

            when (res) {
                is Resource.Success -> {
                    hideProgressDialog()
                    Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Constants.PHONE_EXTRA, res.data!!.data)
                    }.also { startActivity(it); finish() }

                }
                is Resource.Error -> {
                    hideProgressDialog()
                    SignInUpClient.handleGetError(this, res.error)
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
            selectedPhoneUrl.substring(40)
        )
    }

    private fun onPhonesObtainedFromIntent() {
        binding.gifProgressSignUp.visibility = GONE
        mobileAdapter = SelectionAdapter(this)
        initializeRecyclerView(this, binding, mobileAdapter)
        binding.cvSignUseless.visibility = VISIBLE
        mobileAdapter.setData(phones)
    }

    fun onSearchResponseResult(response: SearchResponse) {
        phones = whichModelSuits(response.data.phones) as ArrayList<Phone>
        mobileAdapter = SelectionAdapter(this)
        initializeRecyclerView(this, binding, mobileAdapter)
        binding.cvSignUseless.visibility = VISIBLE
        mobileAdapter.setData(phones)
    }

    private fun launchSignInIntent() {
        val signInIntent = Intent(this, SignInActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (phones.isNotEmpty()) {
                putExtra(Constants.PHONE_LIST, phones)
            }
        }
        startActivity(signInIntent)
        finish()
    }


    private fun setUpSignInUI() {
        restViewModel = ViewModelProvider(this)[RetroViewModel::class.java]
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


    override fun onBackPressed() = doubleBackToExit()

    private fun hideKeyboardInternal() = hideKeyboard(binding.etEmailSignIn, binding.etPasswordSignIn, binding.etUsernameSignIn, binding.etMobileInsertManually)
}
