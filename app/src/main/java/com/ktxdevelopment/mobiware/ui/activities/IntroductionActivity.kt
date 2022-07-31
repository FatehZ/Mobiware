package com.ktxdevelopment.mobiware.ui.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.ActivityIntroductionBinding
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IntroductionActivity : BaseActivity() {
     private lateinit var binding: ActivityIntroductionBinding
     private lateinit var retroViewModel: RetroViewModel
     private lateinit var roomViewModel: LocalViewModel
     private lateinit var navController: NavController

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityIntroductionBinding.inflate(layoutInflater)
          setContentView(binding.root)
          retroViewModel = ViewModelProvider(this)[RetroViewModel::class.java]
          roomViewModel = ViewModelProvider(this)[LocalViewModel::class.java]
          navController = findNavController(R.id.navHostIntro)
     }

     fun getRetroViewModel() = retroViewModel
     fun getLocalViewModel() = roomViewModel

     override fun onBackPressed() {
          if (navController.currentDestination?.id == R.id.fragmentForgotPassword) navController.navigateUp()
          else doubleBackToExit()
     }
}