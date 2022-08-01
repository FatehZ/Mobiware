package com.ktxdevelopment.mobiware.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.ActivityAdditionalBinding
import com.ktxdevelopment.mobiware.util.Constants.FR_FEEDBACK
import com.ktxdevelopment.mobiware.util.Constants.FR_PROFILE
import com.ktxdevelopment.mobiware.util.tryEr
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdditionalActivity : BaseActivity() {
     private lateinit var binding: ActivityAdditionalBinding

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityAdditionalBinding.inflate(layoutInflater).also { setContentView(it.root) }
          setSupportActionBar(binding.toolbarAdditional)
          supportActionBar?.setDisplayHomeAsUpEnabled(true)

          tryEr { handleDestinationOfIntent(intent) }
          binding.toolbarAdditional.setNavigationOnClickListener { finish() }
     }

     private fun handleDestinationOfIntent(i: Intent) {
          when (i.action) {
               FR_FEEDBACK -> {
                    findNavController(R.id.navHostAdditional).navigate(R.id.actionToFeedback)
                    binding.toolbarAdditional.title = getString(R.string.feedback)
               }
               FR_PROFILE -> {
                    findNavController(R.id.navHostAdditional).navigate(R.id.actionToProfile)
                    binding.toolbarAdditional.title = getString(R.string.profile)
               }
          }
     }

     override fun onBackPressed() {
          finish()
     }
}