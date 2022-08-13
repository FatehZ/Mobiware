package com.ktxdevelopment.mobiware.ui.activities

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.main.RemoteClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.ActivityIntroductionBinding
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IntroductionActivity : BaseActivity() {

     private lateinit var binding: ActivityIntroductionBinding
     private lateinit var retroViewModel: RetroViewModel
     private lateinit var roomViewModel: LocalViewModel
     private lateinit var navController: NavController
     private var duration = 3000L
     private val hl = Handler(Looper.getMainLooper())

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityIntroductionBinding.inflate(layoutInflater)
          setContentView(binding.root)
          navController = findNavController(R.id.navHostIntro)
          checkForUpdate()

          retroViewModel = ViewModelProvider(this)[RetroViewModel::class.java]
          roomViewModel = ViewModelProvider(this)[LocalViewModel::class.java]
     }

     fun getRetroViewModel() = retroViewModel
     fun getLocalViewModel() = roomViewModel

     override fun onBackPressed() {
          if (navController.currentDestination?.id == R.id.fragmentForgotPassword) navController.navigateUp()
          else doubleBackToExit()
     }

     private fun checkForUpdate() {
          val map = RemoteClient.checkMustUpdate()
          if (map[Constants.ANY_UPDATE] as Boolean) updateIntent(map[Constants.UPDATE_SIGN] as String)
          else if (FirebaseClient.getCurrentUserId().isEmpty()) signUpIntent()
          else mainIntent()
     }

     private fun mainIntent() {
          splashCheck {
               Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
               }.also { startActivity(it); finish() }
          }
     }

     private fun signUpIntent() {
          splashCheck {
               navController.navigate(R.id.action_fragmentSplash_to_fragmentSignUp)
          }
     }

     private fun updateIntent(sign: String) {
          splashCheck {
               val bundle = Bundle().apply { putString(Constants.UPDATE_SIGN, sign) }
               navController.navigate(R.id.action_fragmentSplash_to_fragmentUpdate, bundle)
          }
     }

     override fun signOut() {
          hideProgressDialog()
          roomViewModel.clearDatabaseWithWork(this)
          (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO)
          Firebase.auth.signOut()
     }
}



fun splashCheck(function: () -> Unit) {
     if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
          Handler(Looper.getMainLooper()).postDelayed(3000) { function() }
     }else function()
}