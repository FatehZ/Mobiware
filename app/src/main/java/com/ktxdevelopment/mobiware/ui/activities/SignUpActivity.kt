package com.ktxdevelopment.mobiware.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.BaseClient.whichModelSuits
import com.ktxdevelopment.mobiware.clients.Preferences.saveUserDetailsToPreferences
import com.ktxdevelopment.mobiware.clients.TextInputClient.validateSignUpInput
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.initializeRecyclerView
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.toastNoConnection
import com.ktxdevelopment.mobiware.clients.ui.SignInUpClient.toastSelectPhone
import com.ktxdevelopment.mobiware.databinding.ActivitySignUpBinding
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.rest.search.SearchResponse
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter.OnMobileClickListener
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.UnknownHostException


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
        restViewModel = ViewModelProvider(this)[RetroViewModel::class.java]

        restViewModel.searchMobile(BaseClient.getDeviceModel())
        binding.btnSignUp.setOnClickListener { signButtonClickListener() }
        binding.btnHaveAccountSignIn.setOnClickListener { launchSignInIntent() }


        if (intent.hasExtra(Constants.PHONE_LIST)) {
            phones = intent.getParcelableArrayListExtra(Constants.PHONE_LIST)!!
            onPhonesObtainedFromIntent()
        }else {

            restViewModel.searchResponse.observe(this) { response ->

                //todo resource type response


//                if (response.isSuccessful) if (response.body() != null) if (response.body()!!.data.phones.isNotEmpty()) {
//                    onSearchResponseResult(response)
//                }
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

    fun onRegisterSuccess(user: FireUser) {

        lifecycleScope.launch {
            saveUserDetailsToPreferences(this@SignUpActivity,user)
        }

        restViewModel.getResponse.observe(this) {

            //todo resource type response



//            if (it.isSuccessful) if (it.body()!=null) {
//                writePhoneToFirestoreInBackground(this)
//
//                val mainIntent = Intent(this, MainActivity::class.java).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
//                    putExtra(Constants.PHONE_EXTRA, it.body()!!.data)
//                }
//
//                hideProgressDialog()
//                startActivity(mainIntent)
//                finish()
//            }
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
        mobileAdapter = SelectionAdapter(this)
        initializeRecyclerView(this, binding, mobileAdapter)
        binding.cvSignUseless.visibility = VISIBLE
        mobileAdapter.setData(phones)
    }

    private fun onSearchResponseResult(response: Response<SearchResponse>) {
        phones = whichModelSuits(response.body()!!.data.phones) as ArrayList<Phone>
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

    override fun onBackPressed() = doubleBackToExit()


    companion object {

        fun writePhoneToFirestoreInBackground(context: Context) {
//            startForegroundService(context, Intent(context, FirestoreService::class.java))



        }
    }
}
