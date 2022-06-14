package com.ktxdevelopment.mobiware.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.BaseClient.whichModelSuits
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.Preferences
import com.ktxdevelopment.mobiware.clients.TextInputClient.validateSignUpInput
import com.ktxdevelopment.mobiware.clients.ui.SignInClient.initializeRecyclerView
import com.ktxdevelopment.mobiware.clients.ui.SignInClient.toastNoConnection
import com.ktxdevelopment.mobiware.clients.ui.SignInClient.toastSelectPhone
import com.ktxdevelopment.mobiware.databinding.ActivitySignUpBinding
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.services.FirestoreService
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter.OnMobileClickListener
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response


@AndroidEntryPoint
class SignUpActivity : BaseActivity(), OnMobileClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var restViewModel: RetroViewModel
    private val TAG = "SIGN_IN_TAG"
    private var isResponsePresent: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var mobileAdapter: SelectionAdapter

    private lateinit var phones: List<Phone>
    private var selectedPhoneUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doubleBackToExit()
        restViewModel = ViewModelProvider(this)[RetroViewModel::class.java]
        restViewModel.searchMobile(BaseClient.getDeviceModel())
        binding.btnSignUp.setOnClickListener { signButtonClickListener() }
        binding.btnHaveAccountSignIn.setOnClickListener { launchSignInIntent() }

        startForegroundService(Intent(this, FirestoreService::class.java))


        restViewModel.searchResponse.observe(this) { response ->
            if (response.isSuccessful) if (response.body() != null) if (response.body()!!.data.phones.isNotEmpty()) {
                onSearchResponseResult(response)
            }
        }
    }


    private fun signButtonClickListener() {
        if (validateSignUpInput(binding.etUsernameSignIn, binding.etPasswordSignIn , binding.etEmailSignIn)) {
            if (hasInternetConnection(this)) {
                if(selectedPhoneUrl != "") {
                    launchRegistration()
                }else toastSelectPhone(this)
            } else toastNoConnection(this)
        }
    }


    override fun onMobileClick(position: Int) {
        selectedPhoneUrl = phones[position].detail
        if (binding.tvSignPhoneModel.visibility == GONE) binding.tvSignPhoneModel.visibility = VISIBLE
        binding.tvSignPhoneModel.text = phones[position].phone_name
    }

    fun onRegisterSuccess() {
        restViewModel.getResponse.observe(this) {
            if (it.isSuccessful) if (it.body()!=null) {

                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.PHONE_EXTRA, it.body()!!.data)
                }

                startForegroundService(Intent(this, FirestoreService::class.java))

                hideProgressDialog()
                startActivity(mainIntent)
                Preferences.storeIsFirstRun(false)
            }
        }
    }


    private fun launchRegistration() {
        showProgressDialog(getString(R.string.registering_user))
        restViewModel.getMobile(selectedPhoneUrl)
        FirebaseClient.registerUserAuth(this, binding.etEmailSignIn.text!!, binding.etPasswordSignIn.text!!)
    }

    private fun onSearchResponseResult(response: Response<SearchResponse>) {
        phones = whichModelSuits(response.body()!!.data.phones)
        mobileAdapter = SelectionAdapter(this)
        initializeRecyclerView(this, binding, mobileAdapter)
        binding.cvSignUseless.visibility = VISIBLE
        mobileAdapter.setData(phones)
    }

    private fun launchSignInIntent() {
        startActivity(Intent(this, SignInActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
    }
}
