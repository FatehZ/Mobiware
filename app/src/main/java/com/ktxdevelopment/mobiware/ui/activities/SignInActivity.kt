package com.ktxdevelopment.mobiware.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.BaseClient.getDeviceModel
import com.ktxdevelopment.mobiware.clients.BaseClient.handler
import com.ktxdevelopment.mobiware.clients.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.BaseClient.whichModelSuits
import com.ktxdevelopment.mobiware.clients.Preferences.saveUserDetailsToPreferences
import com.ktxdevelopment.mobiware.clients.TextInputClient.validateSignInInput
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.initializeRecyclerViewIn
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.toastNoConnection
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.toastSelectPhone
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.ui.activities.SignUpActivity.Companion.writePhoneToFirestoreInBackground
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter.OnMobileClickListener
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import retrofit2.Response


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
        restViewModel = ViewModelProvider(this)[RetroViewModel::class.java]
        val handler = Handler(Looper.getMainLooper())

        restViewModel.searchMobile(getDeviceModel())
        binding.btnSignIn.setOnClickListener { signButtonClickListener() }
        binding.btnNoAccountSignUp.setOnClickListener { launchSignUpIntent() }


        if (intent.hasExtra(Constants.PHONE_LIST)) {
            phones = intent.getParcelableArrayListExtra(Constants.PHONE_LIST)!!
            onPhonesObtainedFromIntentIn()
        }else{
            restViewModel.searchResponse.observe(this) { response ->

                if (response != null) {
                    if (response.isSuccessful) if (response.body() != null) if (response.body()!!.data.phones.isNotEmpty()) {
                        onSearchResponseResult(response)
                    }
                }else{
                    showErrorSnackbar(getString(R.string.no_connection_error))
                    searchAgainIfNoConnection()
                }
            }
        }
    }


    private fun signButtonClickListener() {
        if (validateSignInInput(binding.etPasswordSignIn , binding.etEmailSignIn)) {
            if (hasInternetConnection(this)) {
                if(selectedPhoneUrl != "") {
                    launchSignIn()
                }else toastSelectPhone(this)
            } else toastNoConnection(this)
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

        restViewModel.getResponse.observe(this) {
            if (it.isSuccessful) if (it.body()!=null) {
                writePhoneToFirestoreInBackground(this)

                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.PHONE_EXTRA, it.body()!!.data)
                }
                hideProgressDialog()
                startActivity(mainIntent)
                finish()
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
        mobileAdapter = SelectionAdapter(this)
        initializeRecyclerViewIn(this, binding, mobileAdapter)
        binding.cvSignUselessIn.visibility = VISIBLE
        mobileAdapter.setData(phones)
    }

    private fun onSearchResponseResult(response: Response<SearchResponse>) {
        phones = whichModelSuits(response.body()!!.data.phones) as ArrayList<Phone>
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


    private fun searchAgainIfNoConnection() {
        handler.post(object : Runnable {
            override fun run() {
                restViewModel.searchMobile(getDeviceModel())
                handler.postDelayed(this,600)
            }
        })

    }


    override fun onBackPressed() = doubleBackToExit()
}


//fun loadUserSignIn(loggedUser: FireUser) {
//
//}