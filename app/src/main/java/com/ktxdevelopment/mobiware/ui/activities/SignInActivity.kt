package com.ktxdevelopment.mobiware.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.BaseClient.whichModelSuits
import com.ktxdevelopment.mobiware.clients.TextInputClient.validateSignInInput
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetrofitViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var restViewModel: RetrofitViewModel
    private val TAG = "SIGN_IN_TAG"
    private var isResponsePresent: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var phone: Phone
    private lateinit var phoneDetails: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivity(Intent(this, MainActivity::class.java))

        restViewModel = ViewModelProvider(this)[RetrofitViewModel::class.java]
        restViewModel.searchMobile(BaseClient.getDeviceModel())

        binding.btnSignIn.setOnClickListener { signButtonClickListener() }

        restViewModel.searchResponse.observe(this) { response ->
            if (response.isSuccessful) if (response.body() != null) if (response.body()!!.data.phones.isNotEmpty()) {
                phone = whichModelSuits(response.body()!!.data.phones)
                restViewModel.getMobile(phone.detail)

                restViewModel.getResponse.observe(this@SignInActivity) {
                    if (it.isSuccessful) if (it.body()!= null) {
                        isResponsePresent.value = true
                        phoneDetails = it.body()!!.data
                    }
                }
            }
        }
    }

    private fun signButtonClickListener() {
        if (validateSignInInput(binding.etUsernameSignIn, binding.etUsernameSignIn.text.toString())) {
            if (hasInternetConnection(this)) {
                val mainIntent = Intent(this@SignInActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK) }



                if (isResponsePresent.value == true) {
                    BaseClient.handler.postDelayed(1000) { startActivity(mainIntent) }

                }else{
                    binding.clProgressSignIn.visibility = VISIBLE
                    isResponsePresent.observe(this@SignInActivity) {
                        if (it) {
                            mainIntent.putExtra(Constants.PHONE_EXTRA, phone)
                            mainIntent.putExtra(Constants.PHONE_DETAILS_EXTRA, phoneDetails)
                            //Write to internal db
//                        Preferences.storeIsFirstRun(false)
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}