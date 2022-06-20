package com.ktxdevelopment.mobiware.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient.getDeviceModel
import com.ktxdevelopment.mobiware.clients.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.BaseClient.whichModelSuits
import com.ktxdevelopment.mobiware.clients.Preferences.saveUserDetailsToPreferences
import com.ktxdevelopment.mobiware.clients.TextInputClient
import com.ktxdevelopment.mobiware.clients.TextInputClient.validateSignInInput
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.initializeRecyclerViewIn
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter.OnMobileClickListener
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInActivity : BaseActivity(), OnMobileClickListener {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var restViewModel: RetroViewModel
    private val TAG = "SIGN_IN_TAG"
    private lateinit var mobileAdapter: SelectionAdapter

    private var phones: ArrayList<Phone> = ArrayList()
    private var selectedPhoneUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSignInUI()

        if (intent.hasExtra(Constants.PHONE_LIST)) {
            phones = intent.getParcelableArrayListExtra(Constants.PHONE_LIST)!!
            onPhonesObtainedFromIntentIn()
        }else{
            restViewModel.searchResponse.observe(this) { res ->
                when (res) {
                    is Resource.Success -> {
                        binding.gifProgressSignIn.visibility = GONE
                        SignInUpClient.handleSearchSuccessIn(this, binding, res)
                    }
                    is Resource.Error -> {
                        SignInUpClient.handleErrorIn(this,binding, res.error)
                    }
                    else -> Unit
                }
            }
        }
    }


    private fun signButtonClickListener() {
        hideKeyboardInternal()
        if (validateSignInInput(binding.etPasswordSignIn , binding.etEmailSignIn)) {
            if (hasInternetConnection(this)) {
                if(selectedPhoneUrl != "") {
                    launchSignIn()
                }else showErrorSnackbar(getString(R.string.select_a_phone_error))
            } else showErrorSnackbar(getString(R.string.no_connection_error))
        }
    }

    override fun onMobileClick(position: Int) {
        selectedPhoneUrl = phones[position].detail
        if (binding.tvSignPhoneModelIn.visibility == GONE) binding.tvSignPhoneModelIn.visibility = VISIBLE
        binding.tvSignPhoneModelIn.text = phones[position].phone_name
    }

    fun onSignInSuccess(updatedUser: FireUser) {
        lifecycleScope.launch {
            saveUserDetailsToPreferences(this@SignInActivity, updatedUser)
        }

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


    private fun launchSignIn() {
        showProgressDialog()
        restViewModel.getMobile(selectedPhoneUrl)

        FirebaseClient.signInUserAuth(
            this,
            binding.etEmailSignIn.text!!,
            binding.etPasswordSignIn.text!!,
            selectedPhoneUrl.substring(40)
        )
    }


    private fun onPhonesObtainedFromIntentIn() {
        binding.gifProgressSignIn.visibility = GONE
        mobileAdapter = SelectionAdapter(this)
        initializeRecyclerViewIn(this, binding, mobileAdapter)
        binding.cvSignUselessIn.visibility = VISIBLE
        mobileAdapter.setData(phones)
    }

    fun onSearchResponseResult(response: SearchResponse) {
        phones = whichModelSuits(response.data.phones) as ArrayList<Phone>
        mobileAdapter = SelectionAdapter(this)
        initializeRecyclerViewIn(this, binding, mobileAdapter)
        binding.cvSignUselessIn.visibility = VISIBLE
        mobileAdapter.setData(phones)
    }

    private fun launchSignUpIntent() {
        val signUpIntent = Intent(this, SignUpActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (phones.isNotEmpty()) {
                putExtra(Constants.PHONE_LIST, phones)
            }
        }
        startActivity(signUpIntent)
        finish()
    }


    fun searchAgainIfNoConnection() { restViewModel.searchMobile(getDeviceModel()) }


    private fun setupSignInUI() {
        restViewModel = ViewModelProvider(this)[RetroViewModel::class.java]
        restViewModel.searchMobile(getDeviceModel())
        binding.btnSignIn.setOnClickListener { signButtonClickListener() }
        binding.btnSubmitPhoneModelIn.setOnClickListener { submitPhoneSearchAgain(binding.etMobileInsertManuallyIn) }
        binding.btnNoAccountSignUp.setOnClickListener { launchSignUpIntent() }
        binding.btnForgotPasswordSignIn.setOnClickListener { launchForgotPassword() }
    }

    private fun launchForgotPassword() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }


    private fun submitPhoneSearchAgain(et: TextInputEditText) {
        if (TextInputClient.validatePhoneModel(et)){
            restViewModel.searchMobile(et.text.toString())
            SignInUpClient.phoneNotFoundLayoutDisappear(binding)
        }
    }

    override fun onBackPressed() = doubleBackToExit()

    private fun hideKeyboardInternal() = hideKeyboard(binding.etEmailSignIn, binding.etPasswordSignIn, binding.etMobileInsertManuallyIn)
}