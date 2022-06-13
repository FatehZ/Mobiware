package com.ktxdevelopment.mobiware.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.models.firebase.FireUser

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun loadUserSignIn(loggedUser: FireUser) {

    }
}