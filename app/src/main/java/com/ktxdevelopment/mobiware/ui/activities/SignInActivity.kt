package com.ktxdevelopment.mobiware.ui.activities

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.BaseClient.whichModelSuits
import com.ktxdevelopment.mobiware.clients.FirestoreClient
import com.ktxdevelopment.mobiware.clients.TextInputClient.validateSignInInput
import com.ktxdevelopment.mobiware.clients.ui.SignInClient.initializeRecyclerView
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter.OnMobileClickListener
import com.ktxdevelopment.mobiware.viewmodel.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignInActivity : BaseActivity(), OnMobileClickListener {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var restViewModel: BaseViewModel
    private val TAG = "SIGN_IN_TAG"
    private var isResponsePresent: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var mobileAdapter: SelectionAdapter

    private lateinit var phones: List<Phone>
    private var selectedPhoneUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        restViewModel = ViewModelProvider(this)[BaseViewModel::class.java]
        restViewModel.searchMobile(BaseClient.getDeviceModel())
        binding.btnSignIn.setOnClickListener { signButtonClickListener() }



        restViewModel.searchResponse.observe(this) { response ->
            if (response.isSuccessful) if (response.body() != null) if (response.body()!!.data.phones.isNotEmpty()) {
                phones = whichModelSuits(response.body()!!.data.phones)
                mobileAdapter = SelectionAdapter(this)
                initializeRecyclerView(this, binding, phones, mobileAdapter)
                mobileAdapter.setData(phones)
                binding.tvSignUseless
            }
        }
    }


    private fun signButtonClickListener() {
        if (validateSignInInput(binding.etUsernameSignIn, binding.etPasswordSignIn ,binding.etUsernameSignIn.text.toString())) {
            if (hasInternetConnection(this)) {

                FirestoreClient.registerTest()


            } else {
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onMobileClick(position: Int) {
        selectedPhoneUrl = phones[position].detail
        if (binding.tvSignPhoneModel.visibility == GONE) binding.tvSignPhoneModel.visibility = VISIBLE
        binding.tvSignPhoneModel.text = phones[position].phone_name
    }
}










//                val mainIntent = Intent(this@SignInActivity, MainActivity::class.java).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
//                }


//                if (isResponsePresent.value == true) {
//                    BaseClient.handler.postDelayed(1000) { startActivity(mainIntent) }
//
//                } else {
//                    isResponsePresent.observe(this@SignInActivity) {
//                        if (it) {
//                            mainIntent.putExtra(Constants.PHONE_EXTRA, phones[0])
//                            mainIntent.putExtra(Constants.PHONE_DETAILS_EXTRA, phoneDetails)
//                            //Write to internal db
////                        Preferences.storeIsFirstRun(false)
//                        }
//                    }
//                }