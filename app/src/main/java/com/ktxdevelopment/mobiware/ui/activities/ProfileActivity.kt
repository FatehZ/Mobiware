package com.ktxdevelopment.mobiware.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseClient.loadUserData(this)

        binding.civProfile.setOnClickListener{ galleryResultLauncher.launch(Intent(this, )) }

        binding.toolbarProfile.setNavigationOnClickListener { finish() }
    }


    private val galleryResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            result.data?.let {

                val selectedPhotoUri = result.data!!.data
                binding.civProfile.setImageURI(selectedPhotoUri)
            }
        }
    }
}